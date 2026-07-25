package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiturgicalDayDto(
    @SerialName("events")
    val eventKeys: List<String>, // List of EventKeys associated with this day
    val season: SeasonDto,
    val tune: Int?,
    val lent: Int?,
)
