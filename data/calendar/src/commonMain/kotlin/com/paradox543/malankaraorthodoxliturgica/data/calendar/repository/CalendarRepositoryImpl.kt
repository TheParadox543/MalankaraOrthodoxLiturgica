package com.paradox543.malankaraorthodoxliturgica.data.calendar.repository

import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarSource
import com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping.toCalendarDaysDomain
import com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping.toCalendarWeeksDomain
import com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping.toLiturgicalEventsDetailsDomain
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarDayDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarWeekDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalDataStore
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalEventDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.EventKey
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalCalendarDates
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.MonthEvents
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import kotlin.time.ExperimentalTime
import java.time.LocalDate as JavaLocalDate

@OptIn(ExperimentalTime::class)
class CalendarRepositoryImpl(
    private val calendarSource: CalendarSource,
) : CalendarRepository {
    // Lazy initialization ensures files are read only when first accessed.
    // Asset exceptions are translated at this boundary so callers deal only with
    // AssetReadException / AssetParsingException (both from :data:core).
    private val cachedLiturgicalDates: LiturgicalCalendarDates by lazy {
        try {
            calendarSource.readLiturgicalDates()
        } catch (e: AssetReadException) {
            throw AssetReadException("Could not read assets/calendar/liturgical_calendar.json", e)
        } catch (e: AssetParsingException) {
            throw AssetParsingException("Could not parse assets/calendar/liturgical_calendar.json", e)
        }
    }

    private val cachedLiturgicalData: LiturgicalDataStore by lazy {
        try {
            calendarSource.readLiturgicalData()
        } catch (e: AssetReadException) {
            throw AssetReadException("Could not read assets/calendar/liturgical_data.json", e)
        } catch (e: AssetParsingException) {
            throw AssetParsingException("Could not parse assets/calendar/liturgical_data.json", e)
        }
    }

    /**
     * Internal helper to get event keys for a specific date.
     */
    private fun getEventKeysForDate(day: JavaLocalDate): List<EventKey> {
        return cachedLiturgicalDates[day.year.toString()]
            ?.get(day.monthValue.toString())
            ?.get(day.dayOfMonth.toString())
            ?: emptyList() // Return empty list if no events for the day
    }

    /**
     * Get detailed event information for a given date.
     * @param date The JavaLocalDate object for which to retrieve events.
     * @return A map where keys are EventKeys and values are LiturgicalEventDetails.
     * @throws IllegalArgumentException if an event key found in liturgical_calendar.json
     * is not present in liturgical_data.json.
     */
    fun getEventsForDate(date: JavaLocalDate): List<LiturgicalEventDetailsDto> {
        val eventKeys = getEventKeysForDate(date)
        val eventDetails = mutableListOf<LiturgicalEventDetailsDto>()

        for (key in eventKeys) {
            val details = cachedLiturgicalData[key]
            if (details != null) {
                eventDetails.add(details)
            } else {
                throw IllegalArgumentException("Could not find event key '$key' in liturgical_data.json.")
            }
        }
        return eventDetails
    }

    override fun checkMonthDataExists(
        month: Int,
        year: Int,
    ): Boolean = cachedLiturgicalDates[year.toString()]?.get(month.toString()) is MonthEvents

    /**
     * Loads the calendar data for a specific month and year, structured by weeks.
     * Each week starts on Sunday.
     * @param month The month (1-12). Defaults to current month if null.
     * @param year The year. Defaults to current year if null.
     * @return A list of CalendarWeek objects, each containing 7 CalendarDay objects.
     */
    @OptIn(ExperimentalTime::class)
    override fun loadMonthData(
        month: Int?,
        year: Int?,
    ): List<CalendarWeek> {
        val now =
            kotlin.time.Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val targetYear = year ?: now.year
        val targetMonth = month ?: now.month.number

        require(targetMonth in 1..12) { "Month must be between 1 and 12." }

        val firstDayOfMonth = JavaLocalDate.of(targetYear, targetMonth, 1)
        val lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth())

        // Calculate the first day of the calendar grid (Sunday of the first week)
        var currentDay = firstDayOfMonth
        while (currentDay.dayOfWeek != DayOfWeek.SUNDAY) {
            currentDay = currentDay.minusDays(1)
        }

        val monthData = mutableListOf<CalendarWeekDto>()
        var weekDays = mutableListOf<CalendarDayDto>()

        // Iterate through days, forming weeks
        while (currentDay.isBefore(lastDayOfMonth.plusDays(1)) || currentDay.dayOfWeek != DayOfWeek.SUNDAY) {
            val events = getEventsForDate(currentDay)
            weekDays.add(CalendarDayDto(currentDay, events))

            if (weekDays.size == 7) {
                monthData.add(CalendarWeekDto(weekDays.toList()))
                weekDays = mutableListOf() // Start a new week
            }
            currentDay = currentDay.plusDays(1)
        }

        // Add any remaining days for the last week (if not a full week)
        if (weekDays.isNotEmpty()) {
            while (weekDays.size < 7) {
                weekDays.add(CalendarDayDto(currentDay, emptyList())) // Add placeholder for visual alignment
                currentDay = currentDay.plusDays(1)
            }
            monthData.add(CalendarWeekDto(weekDays.toList()))
        }
        return monthData.toCalendarWeeksDomain()
    }

    fun getUpcomingWeekEventsData(): List<CalendarDayDto> {
        val today =
            kotlin.time.Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                .toJavaLocalDate()
        val weekEvents = mutableListOf<CalendarDayDto>()

        for (i in 0 until 7) {
            val day = today.plusDays(i.toLong())
            val eventDetails = getEventsForDate(day)
            weekEvents.add(CalendarDayDto(day, eventDetails))
        }
        return weekEvents
    }

    /**
     * Get events for the upcoming week starting from today.
     * @return A list of CalendarDay objects for the next 7 days, including their events.
     */
    override fun getUpcomingWeekEvents(): List<CalendarDay> = getUpcomingWeekEventsData().toCalendarDaysDomain()

    override fun getUpcomingWeekEventItems(): List<LiturgicalEventDetails> {
        val weekEvents = getUpcomingWeekEventsData()
        val eventItems = mutableListOf<LiturgicalEventDetailsDto>()
        weekEvents.forEach { day ->
            eventItems.addAll(day.events)
        }
        return eventItems.toLiturgicalEventsDetailsDomain()
    }
}
