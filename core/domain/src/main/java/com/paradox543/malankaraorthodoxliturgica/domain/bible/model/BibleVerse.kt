package com.paradox543.malankaraorthodoxliturgica.domain.bible.model

/**
 * Represents a single verse within a Bible chapter.
 *
 * This is a simple value object containing an identifier and the raw verse
 * text (localized text is supplied by higher-level components when needed).
 *
 * @property id the verse number within a chapter (1-based where applicable).
 * @property verse the verse text content.
 */
data class BibleVerse(
    val id: Int,
    val verse: String,
)