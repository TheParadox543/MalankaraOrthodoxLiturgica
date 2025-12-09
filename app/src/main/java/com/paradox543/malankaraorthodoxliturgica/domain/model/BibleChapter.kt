package com.paradox543.malankaraorthodoxliturgica.domain.model

/**
 * Represents a Bible chapter loaded for display.
 *
 * The [book] property is the book code or name used in storage, [chapter]
 * is the 1-based chapter number and [verses] contains the chapter's verses in
 * order.
 *
 * @property book display name of the book.
 * @property chapter 1-based chapter number.
 * @property verses list of verses in this chapter.
 */
data class BibleChapter(
    val book: String,
    val chapter: Int,
    val verses: List<BibleVerse>,
)