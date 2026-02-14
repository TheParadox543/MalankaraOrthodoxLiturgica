package com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl

import com.paradox543.malankaraorthodoxliturgica.data.datasource.CalendarSource
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toCalendarDaysDomain
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toCalendarWeeksDomain
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toLiturgicalEventsDetailsDomain
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDayDto
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeekDto
import com.paradox543.malankaraorthodoxliturgica.data.model.EventKey
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalCalendarDates
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalDataStore
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetailsData
import com.paradox543.malankaraorthodoxliturgica.data.model.MonthEvents
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import java.io.IOException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val calendarSource: CalendarSource,
) : CalendarRepository {
    // Lazy initialization ensures files are read only when first accessed
    private val cachedLiturgicalDates: LiturgicalCalendarDates by lazy {
        calendarSource.readLiturgicalDates() ?: throw IOException("Could not read from assets/calendar/liturgical_calendar.json")
    }

    private val cachedLiturgicalData: LiturgicalDataStore by lazy {
        calendarSource.readLiturgicalData() ?: throw IOException("Could not read from assets/calendar/liturgical_data.json")
    }

    /**
     * Internal helper to get event keys for a specific date.
     */
    private fun getEventKeysForDate(day: LocalDate): List<EventKey> {
        return cachedLiturgicalDates[day.year.toString()]
            ?.get(day.monthValue.toString())
            ?.get(day.dayOfMonth.toString())
            ?: emptyList() // Return empty list if no events for the day
    }

    /**
     * Get detailed event information for a given date.
     * @param date The LocalDate object for which to retrieve events.
     * @return A map where keys are EventKeys and values are LiturgicalEventDetails.
     * @throws IllegalArgumentException if an event key found in liturgical_calendar.json
     * is not present in liturgical_data.json.
     */
    fun getEventsForDate(date: LocalDate): List<LiturgicalEventDetailsData> {
        val eventKeys = getEventKeysForDate(date)
        val eventDetails = mutableListOf<LiturgicalEventDetailsData>()

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
     * @return A list of CalendarWeekDto objects, each containing 7 CalendarDay objects.
     */
    override fun loadMonthData(
        month: Int?,
        year: Int?,
    ): List<CalendarWeek> {
        val targetYear = year ?: LocalDate.now().year
        val targetMonth = month ?: LocalDate.now().monthValue

        require(targetMonth in 1..12) { "Month must be between 1 and 12." }

        val firstDayOfMonth = LocalDate.of(targetYear, targetMonth, 1)
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
            // Pad with empty days from next month if necessary to complete the last week,
            // though the logic above should ensure full weeks are added already.
            // If the loop finished on Saturday, weekDays would be empty.
            // If it finished mid-week (e.g. month ends on Wednesday), it will contain
            // days from the month and then days from the next month until Saturday.
            while (weekDays.size < 7) {
                weekDays.add(CalendarDayDto(currentDay, emptyList())) // Add placeholder for visual alignment
                currentDay = currentDay.plusDays(1)
            }
            monthData.add(CalendarWeekDto(weekDays.toList()))
        }
        return monthData.toCalendarWeeksDomain()
    }

    fun getUpcomingWeekEventsData(): List<CalendarDayDto> {
        val today = LocalDate.now()
        val weekEvents = mutableListOf<CalendarDayDto>()

        for (i in 0 until 7) {
            val day = today.plusDays(i.toLong())
            val eventDetails = getEventsForDate(day)
            // Python's code appended only if event_details != {}, but here we append
            // CalendarDayDto regardless for consistent list size, and the `events` map
            // will be empty if no events are found, similar to the month data.
            weekEvents.add(CalendarDayDto(day, eventDetails))
        }
        return weekEvents
    }

    /**
     * Get events for the upcoming week starting from today.
     * @return A list of CalendarDayDto objects for the next 7 days, including their events.
     * Only days with events will have non-empty event maps.
     */
    override fun getUpcomingWeekEvents(): List<CalendarDay> = getUpcomingWeekEventsData().toCalendarDaysDomain()

    override fun getUpcomingWeekEventItems(): List<LiturgicalEventDetails> {
        val weekEvents = getUpcomingWeekEventsData()
        val eventItems = mutableListOf<LiturgicalEventDetailsData>()
        weekEvents.forEach { day ->
            eventItems.addAll(day.events)
        }
        return eventItems.toLiturgicalEventsDetailsDomain()
    }
}