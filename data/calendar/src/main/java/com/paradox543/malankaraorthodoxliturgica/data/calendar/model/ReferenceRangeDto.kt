package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class ReferenceRangeDto(
    val startChapter: Int,
    val startVerse: Int,
    val endChapter: Int,
    val endVerse: Int,
)