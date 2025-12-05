package com.paradox543.malankaraorthodoxliturgica.data.model

import java.time.LocalDate

data class CalendarDayDto(
    val date: LocalDate,
    val events: List<LiturgicalEventDetailsData>, // Map of EventKey to its details
)