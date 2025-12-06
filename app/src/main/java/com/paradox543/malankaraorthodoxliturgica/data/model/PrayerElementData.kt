package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
//  import kotlinx.serialization.json.JsonClassDiscriminator

/**
 * Represents an element in a prayer JSON structure.
 *
 * This sealed interface defines various types of prayer elements,
 * each represented by a data class. The elements can be simple text
 * elements like titles and headings, or more complex structures like
 * links to other JSON files or dynamic content blocks.
 *
 * The `type` field in the JSON is used to determine which subclass
 * to instantiate when deserializing.
 */
@Serializable(with = PrayerElementSerializer::class)
sealed interface PrayerElementData {
//  @JsonClassDiscriminator("type")  // Used in initial implementation, but now handled in custom serializer

    // Simple data classes

    /**
     * A title element in a prayer.
     *
     * @param content The text content of the title.
     */
    @Serializable
    @SerialName("title")  // Maps JSON "type": "title" to this class
    data class Title(
        val content: String,
    ) : PrayerElementData

    /**
     * A heading element in a prayer.
     *
     * @param content The text content of the heading.
     */
    @Serializable
    @SerialName("heading")  // Maps JSON "type": "heading" to this class
    data class Heading(
        val content: String,
    ) : PrayerElementData

    /**
     * A subheading element in a prayer.
     *
     * @param content The text content of the subheading.
     */
    @Serializable
    @SerialName("subheading")  // Maps JSON "type": "subheading" to this class
    data class Subheading(
        val content: String,
    ) : PrayerElementData

    /**
     * A prose (regular text) element in a prayer.
     *
     * @param content The text content of the prose.
     */
    @Serializable
    @SerialName("prose")  // Maps JSON "type": "prose" to this class
    data class Prose(
        val content: String,
    ) : PrayerElementData

    /**
     * A song element in a prayer.
     *
     * @param content The text content of the song.
     */
    @Serializable
    @SerialName("song")  // Maps JSON "type": "song" to this class
    data class Song(
        val content: String,
    ) : PrayerElementData

    /**
     * A subtext element in a prayer, used to display responses on the right.
     *
     * @param content The text content of the subtext.
     */
    @Serializable
    @SerialName("subtext")  // Maps JSON "type": "subtext" to this class
    data class Subtext(
        val content: String,
    ) : PrayerElementData

    /**
     * A source/citation element in a prayer.
     *
     * @param content The text content of the source/citation.
     */
    @Serializable
    @SerialName("source")
    data class Source(
        val content: String,
    ) : PrayerElementData

    /**
     * A button element in a prayer, which can link to another prayer or action.
     *
     * @param link The link or action associated with the button.
     * @param label An optional label for the button. If null, a default label is
     * used.
     * @param replace If true, the current prayer is replaced with the linked prayer.
     * If false, the linked prayer is opened on top.
     */
    @Serializable
    @SerialName("button")
    data class Button(
        val link: String,
        val label: String? = null,
        val replace: Boolean = false,
    ) : PrayerElementData

    // Complex data classes

    /**
     * A link element that references another JSON file to be inlined.
     *
     * @param file The filename of the JSON file to link (e.g., "common_prayers.json").
     */
    @Serializable
    @SerialName("link") // Represents a reference to another JSON file to be inlined
    data class Link(
        val file: String, // The filename (e.g., "common_prayers.json")
    ) : PrayerElementData

    /**
     * A collapsible block element that references another JSON file for its content.
     *
     * @param file The filename of the JSON file whose content forms the collapsible block (e.g., "litanies.json").
     */
    @Serializable
    @SerialName("link-collapsible") // Represents a reference to a JSON file whose content forms a collapsible block
    data class LinkCollapsible(
        val file: String, // The filename (e.g., "litanies.json")
    ) : PrayerElementData

    /**
     * A collapsible block element whose content is directly in this JSON.
     *
     * @param title The title of the collapsible block.
     * @param items The nested PrayerElements within this block.
     */
    @Serializable
    @SerialName("collapsible-block") // Represents a collapsible block whose content is directly in this JSON
    data class CollapsibleBlock(
        val title: String,
        val items: List<PrayerElementData>, // The nested PrayerElements within this block
    ) : PrayerElementData

    /**
     * A dynamic song element whose content is fetched based on event and time context.
     *
     * @param eventKey The event context for the song (e.g., "easter", "christmas").
     * @param eventTitle The title for the event (e.g., "Easter").
     * @param timeKey The time context for the song (e.g., "qurbanaGospel", "hoothomo").
     * @param items The items in the dynamic song (usually Subheading and Song).
     */
    @Serializable
    @SerialName("dynamic-song") // Represents a song whose content is fetched dynamically
    data class DynamicSong(
        val eventKey: EventKey, // The event context for the song (e.g., "easter", "christmas")
        val eventTitle: String, // The title for the event (e.g., "Easter")
        val timeKey: String, // The time context for the song (e.g., "qurbanaGospel", "hoothomo")
        val items: List<PrayerElementData>, // The items in the dynamic song (usually Subheading and Song)
    ) : PrayerElementData

    /**
     * A dynamic songs block that stores songs based on time context.
     *
     * @param timeKey The time context for the content (e.g., "afterGospel", "hoothomo").
     * @param items The dynamic songs for this time context.
     * @param defaultContent Fallback content if no dynamic songs match.
     */
    @Serializable
    @SerialName("dynamic-songs-block") // Represents content fetched dynamically based on context
    data class DynamicSongsBlock(
        val timeKey: String, // The time context for the content (e.g., "afterGospel", "hoothomo")
        val items: MutableList<DynamicSong> = mutableListOf(), // The dynamic songs for this time context
        val defaultContent: DynamicSong? = null, // Fallback content if no dynamic songs match
    ) : PrayerElementData

    /**
     * An alternative prayers element that offers multiple prayer options.
     *
     * @param label The label for the alternative prayers section.
     * @param items The PrayerElements for this alternative option.
     */
    @Serializable
    @SerialName("alternative-option")
    data class AlternativeOption(
        val label: String,
        val items: List<PrayerElementData>, // The PrayerElements for this alternative option
    ) : PrayerElementData

    /**
     * A block containing multiple alternative prayer options.
     *
     * @param title The title of the alternative prayers block.
     * @param options The nested PrayerElements within this block.
     */
    @Serializable
    @SerialName("alternative-prayers-block")
    data class AlternativePrayersBlock(
        val title: String,
        val options: List<AlternativeOption>, // The options for alternative prayers
    ) : PrayerElementData

    /**
     * A placeholder element indicating that content is loading.
     *
     * This can be used in the UI to show a loading indicator while
     * dynamic content is being fetched or processed.
     */
    @Serializable
    @SerialName("error")
    data class Error(
        val content: String, // e.g., "Error: File not found."
    ) : PrayerElementData
}