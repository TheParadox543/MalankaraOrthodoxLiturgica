package com.paradox543.malankaraorthodoxliturgica.domain.model

import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetailsData
import java.time.LocalDate

// Custom data classes for the output of loadMonthData and getUpcomingWeekEvents
data class CalendarDay(
    val date: LocalDate,
    val events: List<LiturgicalEventDetailsData>, // Map of EventKey to its details
)