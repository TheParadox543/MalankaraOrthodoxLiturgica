package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class LiturgicalDayDto(
    val eventKeys: List<String>, // List of EventKeys associated with this day
    val season: String,
    val tune: Int?,
    val lent: Int?,
)