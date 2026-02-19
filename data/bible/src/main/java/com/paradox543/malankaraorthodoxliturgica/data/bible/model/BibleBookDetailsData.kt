package com.paradox543.malankaraorthodoxliturgica.data.bible.model

import kotlinx.serialization.Serializable

/** High-level metadata object for a single book.
 */
@Serializable
data class BibleBookDetailsData(
    val book: BibleBookNameData,
    val folder: String,
    val verseCount: List<Int>,
    val chapters: Int = 1,
    val category: String? = null,
    val prefaces: PrefaceContentData? = null,
    val displayTitle: DisplayTextData? = null,
    val ordinal: DisplayTextData? = null,
)