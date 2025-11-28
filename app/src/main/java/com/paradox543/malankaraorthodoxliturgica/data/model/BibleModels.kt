package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

// --- Book Metadata & Preface Models ---
// These classes describe the properties and introductory content of a Bible book.

@Serializable
data class PrefaceContentData(
    val en: List<PrayerElementData>,
    val ml: List<PrayerElementData>,
)

// This is a high-level metadata object for a single book.
@Serializable
data class BibleDetails(
    val book: BibleBookNameData,
    val folder: String,
    val verseCount: List<Int>,
    val chapters: Int = 1,
    val category: String? = null,
    val prefaces: PrefaceContentData? = null,
    val displayTitle: DisplayTextData? = null,
    val ordinal: DisplayTextData? = null,
)

// --- High-Level Application & View Models ---
// These classes represent the final data structures your app will likely use.

// Represents the template file for all preface types.
@Serializable
data class PrefaceTemplatesData(
    val prophets: PrefaceContentData,
    val generalEpistle: PrefaceContentData,
    val paulineEpistle: PrefaceContentData,
)

// Represents a complete reading to be displayed on the screen.
@Serializable
data class BibleReadingData(
    val preface: List<PrayerElementData>? = null,
    val verses: List<BibleVerseData>,
)