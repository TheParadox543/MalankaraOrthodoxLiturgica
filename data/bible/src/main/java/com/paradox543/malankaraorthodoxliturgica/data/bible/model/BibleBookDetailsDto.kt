package com.paradox543.malankaraorthodoxliturgica.data.bible.model

import kotlinx.serialization.Serializable

/** High-level metadata object for a single book.
 */
@Serializable
data class BibleBookDetailsDto(
    val book: BibleBookNameDto,
    val folder: String,
    val verseCount: List<Int>,
    val chapters: Int = 1,
    val category: String? = null,
    val prefaces: PrefaceContentDto? = null,
    val displayTitle: DisplayTextDto? = null,
    val ordinal: DisplayTextDto? = null,
)