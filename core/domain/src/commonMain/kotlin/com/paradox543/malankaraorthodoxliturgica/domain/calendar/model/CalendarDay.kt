package com.paradox543.malankaraorthodoxliturgica.domain.calendar.model

import kotlinx.datetime.LocalDate

/**
 * Represents a day in the calendar.
 *
 * @property date The date associated with this day.
 * @property events A list of [LiturgicalEventDetails] associated with this day.
 */
data class CalendarDay(
    val date: LocalDate,
    val events: List<LiturgicalEventDetails>, // Map of EventKey to its details
)