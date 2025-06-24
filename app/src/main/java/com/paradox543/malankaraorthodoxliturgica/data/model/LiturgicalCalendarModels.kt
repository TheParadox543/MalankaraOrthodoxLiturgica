package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

// Kotlin equivalents of your Python TypeAlias and TypedDict

typealias EventKey = String // Semantically, good to keep, though technically just String
typealias TitleStr = Map<String, String> // e.g., {"en": "English Title", "ml": "Malayalam Title"}

@Serializable
data class LiturgicalEventDetails(
    val type: String,
    val title: TitleStr
)

typealias LiturgicalDataStore = Map<String, LiturgicalEventDetails>

// Structure for liturgical_calendar.json
typealias DayEvents = List<String> // List of EventKeys
typealias MonthEvents = Map<String, DayEvents> // Maps day (e.g., "1") to DayEvents
typealias YearEvents = Map<String, MonthEvents> // Maps month (e.g., "1") to MonthEvents
typealias LiturgicalCalendarDates = Map<String, YearEvents> // Maps year (e.g., "2024") to YearEvents


// Custom data classes for the output of loadMonthData and getUpcomingWeekEvents
data class CalendarDay(
    val date: LocalDate,
    val events: Map<EventKey, LiturgicalEventDetails> // Map of EventKey to its details
)

data class CalendarWeek(
    val days: List<CalendarDay> // List of 7 CalendarDay objects for the week
)