package com.paradox543.malankaraorthodoxliturgica.domain.calendar.model

sealed class WeekItem {
    data class LiturgicalWeek(
        val days: List<LiturgicalDay>,
    ) : WeekItem()

    data class HeaderLabel(
        val name: String,
    ) : WeekItem()
}