package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable(with= PrayerElementSerializer::class)
//@JsonClassDiscriminator("type")  // Use type field in the JSON to deserialize the object
sealed interface PrayerElement {

    // Simple data classes

    @Serializable
    @SerialName("title")  // Maps JSON "type": "title" to this class
    data class Title(
        val content: String
    ): PrayerElement

    @Serializable
    @SerialName("heading")  // Maps JSON "type": "heading" to this class
    data class Heading(
        val content: String
    ): PrayerElement

    @Serializable
    @SerialName("subheading")  // Maps JSON "type": "subheading" to this class
    data class Subheading(
        val content: String
    ): PrayerElement

    @Serializable
    @SerialName("prose")  // Maps JSON "type": "prose" to this class
    data class Prose(
        val content: String
    ): PrayerElement

    @Serializable
    @SerialName("song")  // Maps JSON "type": "song" to this class
    data class Song(
        val content: String
    ): PrayerElement

    @Serializable
    @SerialName("subtext")  // Maps JSON "type": "subtext" to this class
    data class Subtext(
        val content: String
    ): PrayerElement

    @Serializable
    @SerialName("source")
    data class Source(
        val content: String
    ): PrayerElement

    @Serializable
    @SerialName("button")
    data class Button(
        val link: String,
        val label: String? = null,
        val replace: Boolean = false,
    ): PrayerElement

    // Complex data classes

    @Serializable
    @SerialName("link") // Represents a reference to another JSON file to be inlined
    data class Link(
        val file: String // The filename (e.g., "common_prayers.json")
    ) : PrayerElement

    @Serializable
    @SerialName("link-collapsible") // Represents a reference to a JSON file whose content forms a collapsible block
    data class LinkCollapsible(
        val file: String // The filename (e.g., "litanies.json")
    ) : PrayerElement

    @Serializable
    @SerialName("collapsible-block") // Represents a collapsible block whose content is directly in this JSON
    data class CollapsibleBlock(
        val title: String,
        val items: List<PrayerElement> // The nested PrayerElements within this block
    ) : PrayerElement

    // You might also want an "error" element for robust error handling,
    // though ideally, you'd handle errors with exceptions.
    @Serializable
    @SerialName("error")
    data class Error(
        val content: String // e.g., "Error: File not found."
    ) : PrayerElement
}