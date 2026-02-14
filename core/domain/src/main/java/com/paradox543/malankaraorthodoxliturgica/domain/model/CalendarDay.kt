package com.paradox543.malankaraorthodoxliturgica.domain.model

import java.time.LocalDate

// Custom data classes for the output of loadMonthData and getUpcomingWeekEvents
data class CalendarDay(
    val date: LocalDate,
    val events: List<LiturgicalEventDetails>, // Map of EventKey to its details
)