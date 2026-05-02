package com.paradox543.malankaraorthodoxliturgica.feature.calendar.model

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalWeek

data class CalendarUiState(
    val mode: CalendarMode,
    val weeks: List<LiturgicalWeek>,
    val isLoading: Boolean = false,
    val error: String? = null,
)