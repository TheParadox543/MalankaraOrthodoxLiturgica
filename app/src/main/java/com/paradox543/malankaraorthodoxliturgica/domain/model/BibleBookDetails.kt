package com.paradox543.malankaraorthodoxliturgica.domain.model

data class BibleBookDetails(
    val book: BibleBookName,
    val folder: String,
    val chapters: Int,
    val verseCount: List<Int>,
    val category: String? = null,
    val prefaces: PrefaceContent? = null,
    val displayTitle: DisplayText? = null,
    val ordinal: DisplayText? = null,
)