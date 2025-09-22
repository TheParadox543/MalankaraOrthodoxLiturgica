package com.paradox543.malankaraorthodoxliturgica.data.model

import android.util.Log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object PrayerElementSerializer : KSerializer<PrayerElement> {
    // Descriptor for the serializer itself. For polymorphic types, it's often simple.
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PrayerElement")

    // Define a Json instance for internal use by the serializer.
    // It's important that this Json instance does NOT have `serializersModule` configured for PrayerElement,
    // as this custom serializer *is* handling the polymorphism for PrayerElement.
    private val json =
        Json {
            // You might want to ignore unknown keys for the individual data classes
            // so that if a PrayerElement subclass has extra fields, it doesn't crash.
            ignoreUnknownKeys = true
            // Set to true if your JSON might have relaxed syntax (e.g., unquoted keys)
            isLenient = true
            // If a field is nullable but the JSON has null, this helps
            coerceInputValues = true
            // If you had other sealed hierarchies, you might configure them here,
            // but for PrayerElement itself, we handle it manually.
        }

    override fun deserialize(decoder: Decoder): PrayerElement {
        val jsonDecoder =
            decoder as? JsonDecoder
                ?: throw SerializationException("This serializer can only be used with JsonDecoder")

        // 1. Decode the incoming JSON into a generic JsonObject
        // This allows us to inspect its contents (like the 'type' field) first.
        val element = jsonDecoder.decodeJsonElement().jsonObject

        // 2. Extract the 'type' discriminator field
        val type = element["type"]?.jsonPrimitive?.contentOrNull

        // 3. Use a when expression to dispatch to the correct deserializer
        return try {
            when (type) {
                // Simple types
                "title" -> json.decodeFromJsonElement(PrayerElement.Title.serializer(), element)
                "heading" -> json.decodeFromJsonElement(PrayerElement.Heading.serializer(), element)
                "subheading" -> json.decodeFromJsonElement(PrayerElement.Subheading.serializer(), element)
                "prose" -> json.decodeFromJsonElement(PrayerElement.Prose.serializer(), element)
                "song" -> json.decodeFromJsonElement(PrayerElement.Song.serializer(), element)
                "subtext" -> json.decodeFromJsonElement(PrayerElement.Subtext.serializer(), element)
                "source" -> json.decodeFromJsonElement(PrayerElement.Source.serializer(), element)
                "button" -> json.decodeFromJsonElement(PrayerElement.Button.serializer(), element)

                // Complex types
                "collapsible-block" -> json.decodeFromJsonElement(PrayerElement.CollapsibleBlock.serializer(), element)
                "link" -> json.decodeFromJsonElement(PrayerElement.Link.serializer(), element)
                "link-collapsible" -> json.decodeFromJsonElement(PrayerElement.LinkCollapsible.serializer(), element)
                "dynamic-song" -> json.decodeFromJsonElement(PrayerElement.DynamicSong.serializer(), element)
                "dynamic-content" -> json.decodeFromJsonElement(PrayerElement.DynamicContent.serializer(), element)

                // If you explicitly serialize PrayerElement.Error in your JSON, handle it:
                "error" -> json.decodeFromJsonElement(PrayerElement.Error.serializer(), element)

                // --- Fallback for unknown types ---
                else -> {
                    // Create an Error element with informative content
                    PrayerElement.Error("Unknown or invalid PrayerElement type: '$type'.")
                }
            }
        } catch (_: Exception) {
            // Catch any serialization exceptions that might occur even for known types
            // (e.g., missing required fields, type mismatch for a property)
            Log.d("PrayerElementSerializer", "Error parsing PrayerElement: $element")
            PrayerElement.Error("Error parsing PrayerElement: ${element.toString().substring(0, 10)}")
        }
    }

    // --- Serialization part (to convert PrayerElement back to JSON) ---
    // This is less critical for your current problem (reading JSON)
    // but essential if you ever serialize PrayerElement objects back to JSON.
    override fun serialize(
        encoder: Encoder,
        value: PrayerElement,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("This serializer can only be used with JsonEncoder")

        val jsonElement =
            when (value) {
                is PrayerElement.Title -> json.encodeToJsonElement(PrayerElement.Title.serializer(), value)
                is PrayerElement.Heading -> json.encodeToJsonElement(PrayerElement.Heading.serializer(), value)
                is PrayerElement.Subheading -> json.encodeToJsonElement(PrayerElement.Subheading.serializer(), value)
                is PrayerElement.Prose -> json.encodeToJsonElement(PrayerElement.Prose.serializer(), value)
                is PrayerElement.Song -> json.encodeToJsonElement(PrayerElement.Song.serializer(), value)
                is PrayerElement.Subtext -> json.encodeToJsonElement(PrayerElement.Subtext.serializer(), value)
                is PrayerElement.Source -> json.encodeToJsonElement(PrayerElement.Source.serializer(), value)
                is PrayerElement.Button -> json.encodeToJsonElement(PrayerElement.Button.serializer(), value)
                is PrayerElement.CollapsibleBlock -> json.encodeToJsonElement(PrayerElement.CollapsibleBlock.serializer(), value)
                is PrayerElement.Link -> json.encodeToJsonElement(PrayerElement.Link.serializer(), value)
                is PrayerElement.LinkCollapsible -> json.encodeToJsonElement(PrayerElement.LinkCollapsible.serializer(), value)
                is PrayerElement.DynamicSong -> json.encodeToJsonElement(PrayerElement.DynamicSong.serializer(), value)
                is PrayerElement.DynamicContent -> json.encodeToJsonElement(PrayerElement.DynamicContent.serializer(), value)
                is PrayerElement.Error -> json.encodeToJsonElement(PrayerElement.Error.serializer(), value)
            }
        jsonEncoder.encodeJsonElement(jsonElement)
    }
}