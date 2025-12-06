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

object PrayerElementSerializer : KSerializer<PrayerElementData> {
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

    override fun deserialize(decoder: Decoder): PrayerElementData {
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
                "title" -> json.decodeFromJsonElement(PrayerElementData.Title.serializer(), element)
                "heading" -> json.decodeFromJsonElement(PrayerElementData.Heading.serializer(), element)
                "subheading" -> json.decodeFromJsonElement(PrayerElementData.Subheading.serializer(), element)
                "prose" -> json.decodeFromJsonElement(PrayerElementData.Prose.serializer(), element)
                "song" -> json.decodeFromJsonElement(PrayerElementData.Song.serializer(), element)
                "subtext" -> json.decodeFromJsonElement(PrayerElementData.Subtext.serializer(), element)
                "source" -> json.decodeFromJsonElement(PrayerElementData.Source.serializer(), element)
                "button" -> json.decodeFromJsonElement(PrayerElementData.Button.serializer(), element)

                // Complex types
                "collapsible-block" -> json.decodeFromJsonElement(PrayerElementData.CollapsibleBlock.serializer(), element)
                "link" -> json.decodeFromJsonElement(PrayerElementData.Link.serializer(), element)
                "link-collapsible" -> json.decodeFromJsonElement(PrayerElementData.LinkCollapsible.serializer(), element)
                "dynamic-song" -> json.decodeFromJsonElement(PrayerElementData.DynamicSong.serializer(), element)
                "dynamic-songs-block" -> json.decodeFromJsonElement(PrayerElementData.DynamicSongsBlock.serializer(), element)
                "alternative-prayers-block" -> json.decodeFromJsonElement(PrayerElementData.AlternativePrayersBlock.serializer(), element)
                "alternative-option" -> json.decodeFromJsonElement(PrayerElementData.AlternativeOption.serializer(), element)

                // If you explicitly serialize PrayerElement.Error in your JSON, handle it:
                "error" -> json.decodeFromJsonElement(PrayerElementData.Error.serializer(), element)

                // --- Fallback for unknown types ---
                else -> {
                    // Create an Error element with informative content
                    PrayerElementData.Error("Unknown or invalid PrayerElement type: '$type'.")
                }
            }
        } catch (_: Exception) {
            // Catch any serialization exceptions that might occur even for known types
            // (e.g., missing required fields, type mismatch for a property)
            Log.d("PrayerElementSerializer", "Error parsing PrayerElement: $element")
            PrayerElementData.Error("Error parsing PrayerElement: ${element.toString().substring(0, 10)}")
        }
    }

    // --- Serialization part (to convert PrayerElement back to JSON) ---
    // This is less critical for your current problem (reading JSON)
    // but essential if you ever serialize PrayerElement objects back to JSON.
    override fun serialize(
        encoder: Encoder,
        value: PrayerElementData,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("This serializer can only be used with JsonEncoder")

        val jsonElement =
            when (value) {
                is PrayerElementData.Title -> json.encodeToJsonElement(PrayerElementData.Title.serializer(), value)
                is PrayerElementData.Heading -> json.encodeToJsonElement(PrayerElementData.Heading.serializer(), value)
                is PrayerElementData.Subheading -> json.encodeToJsonElement(PrayerElementData.Subheading.serializer(), value)
                is PrayerElementData.Prose -> json.encodeToJsonElement(PrayerElementData.Prose.serializer(), value)
                is PrayerElementData.Song -> json.encodeToJsonElement(PrayerElementData.Song.serializer(), value)
                is PrayerElementData.Subtext -> json.encodeToJsonElement(PrayerElementData.Subtext.serializer(), value)
                is PrayerElementData.Source -> json.encodeToJsonElement(PrayerElementData.Source.serializer(), value)
                is PrayerElementData.Button -> json.encodeToJsonElement(PrayerElementData.Button.serializer(), value)
                is PrayerElementData.CollapsibleBlock -> json.encodeToJsonElement(PrayerElementData.CollapsibleBlock.serializer(), value)
                is PrayerElementData.Link -> json.encodeToJsonElement(PrayerElementData.Link.serializer(), value)
                is PrayerElementData.LinkCollapsible -> json.encodeToJsonElement(PrayerElementData.LinkCollapsible.serializer(), value)
                is PrayerElementData.DynamicSong -> json.encodeToJsonElement(PrayerElementData.DynamicSong.serializer(), value)
                is PrayerElementData.DynamicSongsBlock -> json.encodeToJsonElement(PrayerElementData.DynamicSongsBlock.serializer(), value)
                is PrayerElementData.AlternativePrayersBlock ->
                    json.encodeToJsonElement(
                        PrayerElementData.AlternativePrayersBlock.serializer(),
                        value,
                    )
                is PrayerElementData.AlternativeOption -> json.encodeToJsonElement(PrayerElementData.AlternativeOption.serializer(), value)
                is PrayerElementData.Error -> json.encodeToJsonElement(PrayerElementData.Error.serializer(), value)
            }
        jsonEncoder.encodeJsonElement(jsonElement)
    }
}