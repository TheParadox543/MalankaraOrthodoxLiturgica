package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class LiturgicalYearlyDatesDto(
    val version: String,
    val liturgicalYear: String,
    val data: Map<String, LiturgicalDayDto>,
)