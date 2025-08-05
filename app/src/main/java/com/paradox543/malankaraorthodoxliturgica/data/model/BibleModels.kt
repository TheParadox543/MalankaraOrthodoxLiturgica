package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

// --- Core Bible Structure Models (Bottom-up) ---
// These classes represent the physical structure of the Bible text itself.

@Serializable
data class Verse(
    val Verseid: String,
    val Verse: String
)

@Serializable
data class Chapter(
    val Verse: List<Verse>
)

@Serializable
data class Book(
    val Chapter: List<Chapter>
)

@Serializable
data class BibleRoot(
    val Book: List<Book>
)


// --- Book Metadata & Preface Models ---
// These classes describe the properties and introductory content of a Bible book.

@Serializable
data class BookName(
    val en: String,
    val ml: String
)

@Serializable
data class DisplayText(
    val en: String,
    val ml: String,
)

@Serializable
data class PrefaceContent(
    val en: List<PrayerElement>,
    val ml: List<PrayerElement>,
)

// This is a high-level metadata object for a single book.
@Serializable
data class BibleDetails(
    val book: BookName,
    val chapters: Int = 1,
    val category: String? = null,
    val prefaces: PrefaceContent? = null,
    val displayTitle: DisplayText? = null,
    val ordinal: DisplayText? = null,
)


// --- High-Level Application & View Models ---
// These classes represent the final data structures your app will likely use.

// Represents the template file for all preface types.
@Serializable
data class PrefaceTemplates(
    val prophets: PrefaceContent,
    val generalEpistle: PrefaceContent,
    val paulineEpistle: PrefaceContent,
)

// Represents a complete reading to be displayed on the screen.
@Serializable
data class BibleReading(
    val preface: List<PrayerElement>? = null,
    val verses: List<Verse>,
)