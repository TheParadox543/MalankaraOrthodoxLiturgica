package com.paradox543.malankaraorthodoxliturgica.feature.calendar.model

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.WeekItem

data class CalendarUiState(
    val mode: CalendarMode,
    val weeks: List<WeekItem>,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedDay: LiturgicalDay? = null,
    val selectedDayEvents: List<LiturgicalEventDetails> = emptyList(),
    val isSelectedDayEventsLoading: Boolean = false,
    val hasPreviousSeason: Boolean = false,
    val hasNextSeason: Boolean = false,
)
