package com.paradox543.malankaraorthodoxliturgica.data.model

import com.paradox543.malankaraorthodoxliturgica.domain.model.LiturgicalEventDetails
import java.time.LocalDate

typealias EventKey = String // Semantically, good to keep, though technically just String

typealias LiturgicalDataStore = Map<String, LiturgicalEventDetailsData>

// Structure for liturgical_calendar.json
typealias DayEvents = List<EventKey> // List of EventKeys
typealias MonthEvents = Map<String, DayEvents> // Maps day (e.g., "1") to DayEvents
typealias YearEvents = Map<String, MonthEvents> // Maps month (e.g., "1") to MonthEvents
typealias LiturgicalCalendarDates = Map<String, YearEvents> // Maps year (e.g., "2024") to YearEvents

// Custom data classes for the output of loadMonthData and getUpcomingWeekEvents
data class CalendarDay(
    val date: LocalDate,
    val events: List<LiturgicalEventDetailsData>, // Map of EventKey to its details
)

data class CalendarWeek(
    val days: List<CalendarDay>, // List of 7 CalendarDay objects for the week
)