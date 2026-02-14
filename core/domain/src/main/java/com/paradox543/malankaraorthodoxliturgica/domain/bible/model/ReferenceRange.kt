package com.paradox543.malankaraorthodoxliturgica.domain.bible.model

data class ReferenceRange(
    val startChapter: Int,
    val startVerse: Int,
    val endChapter: Int,
    val endVerse: Int,
)