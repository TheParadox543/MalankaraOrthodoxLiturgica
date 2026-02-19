package com.paradox543.malankaraorthodoxliturgica.data.bible.model

import kotlinx.serialization.Serializable

@Serializable
data class ReferenceRangeData(
    val startChapter: Int,
    val startVerse: Int,
    val endChapter: Int,
    val endVerse: Int,
)