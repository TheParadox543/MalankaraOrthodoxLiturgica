package com.paradox543.malankaraorthodoxliturgica.domain.model

/**
 * Domain copy of PrayerElement kept in the core/domain layer.
 * This mirrors the structure of the data model but intentionally
 * does not include serialization annotations or data-layer concerns.
 */
sealed interface PrayerElementDomain {
    // Simple data classes
    data class Title(
        val content: String,
    ) : PrayerElementDomain

    data class Heading(
        val content: String,
    ) : PrayerElementDomain

    data class Subheading(
        val content: String,
    ) : PrayerElementDomain

    data class Prose(
        val content: String,
    ) : PrayerElementDomain

    data class Song(
        val content: String,
    ) : PrayerElementDomain

    data class Subtext(
        val content: String,
    ) : PrayerElementDomain

    data class Source(
        val content: String,
    ) : PrayerElementDomain

    data class Button(
        val link: String,
        val label: String? = null,
        val replace: Boolean = false,
    ) : PrayerElementDomain

    // Complex data classes
    data class Link(
        val file: String,
    ) : PrayerElementDomain

    data class LinkCollapsible(
        val file: String,
    ) : PrayerElementDomain

    data class CollapsibleBlock(
        val title: String,
        val items: List<PrayerElementDomain>,
    ) : PrayerElementDomain

    data class DynamicSong(
        val eventKey: EventKey,
        val eventTitle: String,
        val timeKey: String,
        val items: List<PrayerElementDomain>,
    ) : PrayerElementDomain

    data class DynamicSongsBlock(
        val timeKey: String,
        val items: MutableList<DynamicSong> = mutableListOf(),
        val defaultContent: DynamicSong? = null,
    ) : PrayerElementDomain

    data class AlternativeOption(
        val label: String,
        val items: List<PrayerElementDomain>,
    ) : PrayerElementDomain

    data class AlternativePrayersBlock(
        val title: String,
        val options: List<AlternativeOption>,
    ) : PrayerElementDomain

    data class Error(
        val content: String,
    ) : PrayerElementDomain
}
