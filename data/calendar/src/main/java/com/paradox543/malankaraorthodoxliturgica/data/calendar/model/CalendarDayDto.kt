package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import java.time.LocalDate

data class CalendarDayDto(
    val date: LocalDate,
    val events: List<LiturgicalEventDetailsDto>, // Map of EventKey to its details
)