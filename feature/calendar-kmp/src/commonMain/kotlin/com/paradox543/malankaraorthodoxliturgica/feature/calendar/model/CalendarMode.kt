package com.paradox543.malankaraorthodoxliturgica.feature.calendar.model

sealed class CalendarMode {
    /**
     * Represents a Season in a liturgical year
     */
    data class Season(
        val liturgicalYear: String,
        val name: String,
    ) : CalendarMode()

    /**
     * Represents a Month in a liturgical year
     */
    data class Month(
        val year: Int,
        val month: Int,
    ) : CalendarMode()
}