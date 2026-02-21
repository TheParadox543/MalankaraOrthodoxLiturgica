package com.paradox543.malankaraorthodoxliturgica.domain.prayer.model

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.EventKey

/**
 * Domain copy of PrayerElement kept in the core/domain layer.
 * This mirrors the structure of the data model but intentionally
 * does not include serialization annotations or data-layer concerns.
 */
sealed interface PrayerElement {
    // Simple data classes
    data class Title(
        val content: String,
    ) : PrayerElement

    data class Heading(
        val content: String,
    ) : PrayerElement

    data class Subheading(
        val content: String,
    ) : PrayerElement

    data class Prose(
        val content: String,
    ) : PrayerElement

    data class Song(
        val content: String,
    ) : PrayerElement

    data class Subtext(
        val content: String,
    ) : PrayerElement

    data class Source(
        val content: String,
    ) : PrayerElement

    data class Button(
        val link: String,
        val label: String? = null,
        val replace: Boolean = false,
    ) : PrayerElement

    // Complex data classes
    data class Link(
        val file: String,
    ) : PrayerElement

    data class LinkCollapsible(
        val file: String,
    ) : PrayerElement

    data class CollapsibleBlock(
        val title: String,
        val items: List<PrayerElement>,
    ) : PrayerElement

    data class DynamicSong(
        val eventKey: EventKey,
        val eventTitle: String,
        val timeKey: String,
        val items: List<PrayerElement>,
    ) : PrayerElement

    data class DynamicSongsBlock(
        val timeKey: String,
        val items: MutableList<DynamicSong> = mutableListOf(),
        val defaultContent: DynamicSong? = null,
    ) : PrayerElement

    data class AlternativeOption(
        val label: String,
        val items: List<PrayerElement>,
    ) : PrayerElement

    data class AlternativePrayersBlock(
        val title: String,
        val options: List<AlternativeOption>,
    ) : PrayerElement

    data class Error(
        val content: String,
    ) : PrayerElement
}
