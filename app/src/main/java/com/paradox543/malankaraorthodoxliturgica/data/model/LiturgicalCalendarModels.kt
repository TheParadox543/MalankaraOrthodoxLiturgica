package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate


typealias EventKey = String // Semantically, good to keep, though technically just String

@Serializable
data class TitleStr(
    val en: String,
    val ml: String? = null,
)

@Serializable
data class ReferenceRange(
    val startChapter: Int,
    val startVerse: Int,
    val endChapter: Int,
    val endVerse: Int,
)

@Serializable
data class BibleReference(
    val bookNumber: Int,
    val ranges: List<ReferenceRange>,
)

@Serializable
data class BibleReadings(
    val vespersGospel: List<BibleReference>? =  null,
    val matinsGospel: List<BibleReference>? = null,
    val primeGospel: List<BibleReference>? = null,
    val oldTestament: List<BibleReference>? = null,
    val generalEpistle: List<BibleReference>? = null,
    val paulEpistle: List<BibleReference>? = null,
    val gospel: List<BibleReference>? = null,
)

@Serializable
data class LiturgicalEventDetails(
    val type: String,
    val title: TitleStr,
    val bibleReadings: BibleReadings? = null,
    val specialSongsKey: String? = null,
    val startedYear: Int? = null,
)

typealias LiturgicalDataStore = Map<String, LiturgicalEventDetails>

// Structure for liturgical_calendar.json
typealias DayEvents = List<EventKey> // List of EventKeys
typealias MonthEvents = Map<String, DayEvents> // Maps day (e.g., "1") to DayEvents
typealias YearEvents = Map<String, MonthEvents> // Maps month (e.g., "1") to MonthEvents
typealias LiturgicalCalendarDates = Map<String, YearEvents> // Maps year (e.g., "2024") to YearEvents


// Custom data classes for the output of loadMonthData and getUpcomingWeekEvents
data class CalendarDay(
    val date: LocalDate,
    val events: List<LiturgicalEventDetails> // Map of EventKey to its details
)

data class CalendarWeek(
    val days: List<CalendarDay> // List of 7 CalendarDay objects for the week
)