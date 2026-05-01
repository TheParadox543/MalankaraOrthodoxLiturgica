package com.paradox543.malankaraorthodoxliturgica.domain.calendar.model

import kotlinx.datetime.LocalDate

data class LiturgicalDay(
    val date: LocalDate,
    val eventKeys: List<String>, // List of EventKeys associated with this day
    val season: String,
    val tune: Int?,
    val lent: Int?,
)
