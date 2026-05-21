package com.paradox543.malankaraorthodoxliturgica.domain.calendar.model

import kotlinx.datetime.LocalDate

data class LiturgicalDay(
    val date: LocalDate,
    val liturgicalYear: String?,
    val eventKeys: List<String>, // List of EventKeys associated with this day
    val season: String?,
    val tune: Int?,
    val lent: Int?,
    val isToday: Boolean = false,
) {
    companion object {
        fun empty(date: LocalDate) =
            LiturgicalDay(
                date = date,
                liturgicalYear = null,
                eventKeys = emptyList(),
                season = null,
                tune = null,
                lent = null,
            )
    }
}