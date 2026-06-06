package com.paradox543.malankaraorthodoxliturgica.feature.calendar.model

sealed class CalendarMode {
    data class Season(
        val liturgicalYear: String,
        val name: String,
    ) : CalendarMode()

    data class Month(
        val year: Int,
        val month: Int,
    ) : CalendarMode()
}