package com.paradox543.malankaraorthodoxliturgica.data.calendar.repository

import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarSource
import com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping.toCalendarDaysDomain
import com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping.toCalendarWeeksDomain
import com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping.toLiturgicalEventsDetailsDomain
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarDayDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarWeekDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalDataStore
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalEventDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalYearlyDatesDto
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarData
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.EventKey
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalCalendarDates
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.MonthEvents
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.SeasonName
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.WeekItem
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CalendarRepositoryImpl(
    private val calendarSource: CalendarSource,
) : CalendarRepository {
    private val cacheMutex = Mutex()
    private var cachedLiturgicalDates: LiturgicalCalendarDates? = null
    private var cachedLiturgicalData: LiturgicalDataStore? = null
    private val cachedEventDetailsByKeys = mutableMapOf<List<String>, List<LiturgicalEventDetails>>()
    private var calendarData: CalendarData = emptyMap()

    private suspend fun initializeLiturgicalDatesIfNeeded() {
        if (cachedLiturgicalDates != null) return
        cacheMutex.withLock {
            if (cachedLiturgicalDates != null) return@withLock
            try {
                cachedLiturgicalDates = calendarSource.readLiturgicalDates()
            } catch (e: AssetReadException) {
                throw AssetReadException("Could not read assets/calendar/liturgical_calendar.json", e)
            } catch (e: AssetParsingException) {
                throw AssetParsingException("Could not parse assets/calendar/liturgical_calendar.json", e)
            }
        }
    }

    private suspend fun initializeLiturgicalDataIfNeeded() {
        if (cachedLiturgicalData != null) return
        cacheMutex.withLock {
            if (cachedLiturgicalData != null) return@withLock
            try {
                cachedLiturgicalData = calendarSource.readLiturgicalData()
            } catch (e: AssetReadException) {
                throw AssetReadException("Could not read assets/calendar/liturgical_data.json", e)
            } catch (e: AssetParsingException) {
                throw AssetParsingException("Could not parse assets/calendar/liturgical_data.json", e)
            }
        }
    }

    private suspend fun initializeCalendarDataIfNeeded() {
        if (calendarData.isNotEmpty()) return
        cacheMutex.withLock {
            if (calendarData.isNotEmpty()) return@withLock
            if (calendarData.isEmpty()) {
                try {
                    loadCalendar()
                    AppLogger.d("CalendarRepo") { "Calendar data loaded: ${calendarData.size} entries" }
                } catch (e: Exception) {
                    AppLogger.e("CalendarRepo") { "Failed to load calendarData: ${e.message}" }
                    throw e
                }
            }
        }
    }

    private fun getLiturgicalDates(): LiturgicalCalendarDates = cachedLiturgicalDates ?: error("Calendar not initialized")

    private fun getLiturgicalData(): LiturgicalDataStore = cachedLiturgicalData ?: error("Calendar not initialized")

    private suspend fun loadCalendar() {
        val yearlyData: List<LiturgicalYearlyDatesDto> = calendarSource.loadAllYears()
        AppLogger.d("CalendarRepo") { "loadCalendar: yearlyData.size=${yearlyData.size}" }

        calendarData =
            yearlyData
                .flatMap { yearlyDatesDto ->
                    yearlyDatesDto.data.entries.map { (dateString, dto) ->
                        Triple(yearlyDatesDto.liturgicalYear, dateString, dto)
                    }
                }.associate { (liturgicalYear, dateString, dto) ->

                    val date = LocalDateTime.parse(dateString).date

                    date to
                        LiturgicalDay(
                            date = date,
                            liturgicalYear = liturgicalYear,
                            eventKeys = dto.eventKeys,
                            seasonName = dto.season.toDomain(),
                            tune = dto.tune,
                            lent = dto.lent,
                        )
                }
    }

    /**
     * Internal helper to get event keys for a specific date.
     */
    private fun getEventKeysForDate(day: LocalDate): List<EventKey> {
        return getLiturgicalDates()[day.year.toString()]
            ?.get(day.month.number.toString())
            ?.get(day.day.toString())
            ?: emptyList() // Return empty list if no events for the day
    }

    /**
     * Get detailed event information for a given date.
     * @param date The JavaLocalDate object for which to retrieve events.
     * @return A map where keys are EventKeys and values are LiturgicalEventDetails.
     * @throws IllegalArgumentException if an event key found in liturgical_calendar.json
     * is not present in liturgical_data.json.
     */
    fun getEventsForDate(date: LocalDate): List<LiturgicalEventDetailsDto> {
        val eventKeys = getEventKeysForDate(date)
        val eventDetails = mutableListOf<LiturgicalEventDetailsDto>()

        for (key in eventKeys) {
            val details = getLiturgicalData()[key]
            if (details != null) {
                eventDetails.add(details)
            } else {
                throw IllegalArgumentException("Could not find event key '$key' in liturgical_data.json.")
            }
        }
        return eventDetails
    }

    override suspend fun getDay(day: LocalDate): LiturgicalDay? {
        initializeCalendarDataIfNeeded()
        return calendarData[day]
    }

    override suspend fun getRange(
        start: LocalDate,
        end: LocalDate,
    ): List<LiturgicalDay> {
        initializeCalendarDataIfNeeded()
        return calendarData
            .filterKeys { it in start..end }
//            .toSortedMap()
            .values
            .toList()
    }

    fun loadWeeks(days: List<LiturgicalDay>): List<WeekItem> {
        if (days.isEmpty()) return emptyList()

        val sortedDays = days.sortedBy { it.date }
        val result = mutableListOf<WeekItem>()
        var currentWeek = mutableListOf<LiturgicalDay>()
        var lastMonthName: String? = null
        val today =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date

        fun monthNameFor(day: LiturgicalDay): String =
            day.date.month.name
                .lowercase()
                .replaceFirstChar { it.uppercase() }

        fun firstRealDay(week: List<LiturgicalDay>): LiturgicalDay? = week.firstOrNull { it.seasonName != null } ?: week.firstOrNull()

        fun addWeekIfNeeded() {
            if (currentWeek.isEmpty()) return

            val week = currentWeek.toList()
            val anchorDay = firstRealDay(week) ?: return
            val monthName = monthNameFor(anchorDay)

            if (monthName != lastMonthName) {
                result.add(WeekItem.HeaderLabel(monthName))
                lastMonthName = monthName
            }

            result.add(WeekItem.LiturgicalWeek(week))
            currentWeek = mutableListOf()
        }

        // Step 1: pad first week
        val firstDay = sortedDays.first()
        val firstDayOfWeek =
            firstDay.date
                .dayOfWeek
                .let { (it.ordinal + 1) % 7 } // Sunday = 0

        repeat(firstDayOfWeek) {
            val date = firstDay.date.minus(firstDayOfWeek - it, DateTimeUnit.DAY)
            currentWeek.add(
                LiturgicalDay.empty(date),
            )
        }

        // Step 2: fill weeks
        for (day in sortedDays) {
            currentWeek.add(day.copy(isToday = day.date == today))

            if (currentWeek.size == 7) {
                addWeekIfNeeded()
            }
        }

        // Step 3: pad last week
        if (currentWeek.isNotEmpty()) {
            while (currentWeek.size < 7) {
                val date = currentWeek.last().date.plus(1, DateTimeUnit.DAY)
                currentWeek.add(
                    LiturgicalDay.empty(date),
                )
            }
            addWeekIfNeeded()
        }

        return result
    }

    override suspend fun getSeasonDays(
        liturgicalYear: String,
        seasonName: SeasonName,
    ): List<LiturgicalDay> {
        initializeCalendarDataIfNeeded()
        val days =
            calendarData
                .filterValues { it.seasonName == seasonName && it.liturgicalYear == liturgicalYear }
//            .toSortedMap(compareBy { it })
                .values
                .toList()
        AppLogger.d("getSeasonDays") { "Found ${days.size} days for seasonName '$seasonName'" }
        return days
    }

    override suspend fun getSeasonWeeks(
        liturgicalYear: String,
        seasonName: SeasonName,
    ): List<WeekItem> {
        val days = getSeasonDays(liturgicalYear, seasonName)
        return loadWeeks(days)
    }

    override suspend fun getMonthDays(
        year: Int,
        month: Int,
    ): List<LiturgicalDay> {
        val start = LocalDate(year, month, 1)
        val end = start.plus(DatePeriod(months = 1)).minus(DatePeriod(days = 1)) // Get last day of the month

        return getRange(start, end)
    }

    override suspend fun getMonthWeeks(
        year: Int,
        month: Int,
    ): List<WeekItem> {
        val days = getMonthDays(year, month)
        return loadWeeks(days)
    }

    override suspend fun getUpcomingDays(count: Int): List<LiturgicalDay> {
        initializeCalendarDataIfNeeded()
        val today =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date

        return calendarData
            .filterKeys { it >= today }
//            .toSortedMap()
            .values
            .take(count)
    }

    override suspend fun checkMonthDataExists(
        month: Int,
        year: Int,
    ): Boolean {
        initializeLiturgicalDatesIfNeeded()
        return getLiturgicalDates()[year.toString()]?.get(month.toString()) is MonthEvents
    }

    /**
     * Loads the calendar data for a specific month and year, structured by weeks.
     * Each week starts on Sunday.
     * @param month The month (1-12). Defaults to current month if null.
     * @param year The year. Defaults to current year if null.
     * @return A list of CalendarWeek objects, each containing 7 CalendarDay objects.
     */
    @OptIn(ExperimentalTime::class)
    override suspend fun loadMonthData(
        month: Int?,
        year: Int?,
    ): List<CalendarWeek> {
        initializeLiturgicalDatesIfNeeded()
        initializeLiturgicalDataIfNeeded()
        val now =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val targetYear = year ?: now.year
        val targetMonth = month ?: now.month.number

        require(targetMonth in 1..12) { "Month must be between 1 and 12." }

        val firstDayOfMonth = LocalDate(targetYear, targetMonth, 1)
        val daysInMonth =
            when {
                targetMonth == 12 -> 31
                else -> LocalDate(targetYear, targetMonth + 1, 1).minus(DatePeriod(days = 1)).day
            }
        val lastDayOfMonth = LocalDate(targetYear, targetMonth, daysInMonth)

        // Calculate the first day of the calendar grid (Sunday of the first week)
        var currentDay = firstDayOfMonth
        while (currentDay.dayOfWeek != DayOfWeek.SUNDAY) {
            currentDay = currentDay.minus(1, DateTimeUnit.DAY)
        }

        val monthData = mutableListOf<CalendarWeekDto>()
        var weekDays = mutableListOf<CalendarDayDto>()

        // Iterate through days, forming weeks
        while (currentDay <= lastDayOfMonth || currentDay.dayOfWeek != DayOfWeek.SUNDAY) {
            val events = getEventsForDate(currentDay)
            weekDays.add(CalendarDayDto(currentDay, events))

            if (weekDays.size == 7) {
                monthData.add(CalendarWeekDto(weekDays.toList()))
                weekDays = mutableListOf() // Start a new week
            }
            currentDay = currentDay.plus(1, DateTimeUnit.DAY)
        }

        // Add any remaining days for the last week (if not a full week)
        if (weekDays.isNotEmpty()) {
            while (weekDays.size < 7) {
                weekDays.add(CalendarDayDto(currentDay, emptyList())) // Add placeholder for visual alignment
                currentDay = currentDay.plus(1, DateTimeUnit.DAY)
            }
            monthData.add(CalendarWeekDto(weekDays.toList()))
        }
        return monthData.toCalendarWeeksDomain()
    }

    fun getUpcomingWeekEventsData(): List<CalendarDayDto> {
        val today =
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val weekEvents = mutableListOf<CalendarDayDto>()

        for (i in 0 until 7) {
            val day = today.plus(i, DateTimeUnit.DAY)
            val eventDetails = getEventsForDate(day)
            weekEvents.add(CalendarDayDto(day, eventDetails))
        }
        return weekEvents
    }

    /**
     * Get events for the upcoming week starting from today.
     * @return A list of CalendarDay objects for the next 7 days, including their events.
     */
    override suspend fun getUpcomingWeekEvents(): List<CalendarDay> {
        initializeLiturgicalDatesIfNeeded()
        initializeLiturgicalDataIfNeeded()
        return getUpcomingWeekEventsData().toCalendarDaysDomain()
    }

    override suspend fun getUpcomingWeekEventItems(): List<LiturgicalEventDetails> {
        initializeLiturgicalDatesIfNeeded()
        initializeLiturgicalDataIfNeeded()
        val weekEvents = getUpcomingWeekEventsData()
        val eventItems = mutableListOf<LiturgicalEventDetailsDto>()
        weekEvents.forEach { day ->
            eventItems.addAll(day.events)
        }
        return eventItems.toLiturgicalEventsDetailsDomain()
    }

    override suspend fun getEvents(eventKeys: List<String>): List<LiturgicalEventDetails> {
        if (eventKeys.isEmpty()) return emptyList()

        val cacheKey = eventKeys.toList()
        val cachedEvents = cacheMutex.withLock { cachedEventDetailsByKeys[cacheKey] }
        if (cachedEvents != null) return cachedEvents

        initializeLiturgicalDataIfNeeded()
        val events =
            eventKeys
                .map { key ->
                    getLiturgicalData()[key]
                        ?: throw IllegalArgumentException("Could not find event key '$key' in liturgical_data.json.")
                }.toLiturgicalEventsDetailsDomain()

        return cacheMutex.withLock {
            cachedEventDetailsByKeys.getOrPut(cacheKey) { events }
        }
    }

    override suspend fun hasLiturgicalYear(liturgicalYear: String): Boolean {
        initializeCalendarDataIfNeeded()
        return calendarData.values.any { it.liturgicalYear == liturgicalYear }
    }
}
