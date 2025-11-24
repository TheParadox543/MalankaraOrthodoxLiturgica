package com.paradox543.malankaraorthodoxliturgica.domain.model

data class BibleChapter(
    val book: String,
    val chapter: Int,
    val verses: List<BibleVerse>,
)