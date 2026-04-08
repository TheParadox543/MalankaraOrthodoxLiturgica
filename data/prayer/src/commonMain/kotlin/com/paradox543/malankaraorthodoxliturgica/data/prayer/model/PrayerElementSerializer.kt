package com.paradox543.malankaraorthodoxliturgica.data.prayer.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

object PrayerElementSerializer : KSerializer<PrayerElementDto> {
    // Descriptor for the serializer itself. For polymorphic types, it's often simple.
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PrayerElement")

    // Define a JSON instance for internal use by the serializer.
    // It's important that this JSON instance does NOT have `serializersModule` configured for PrayerElement,
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

    override fun deserialize(decoder: Decoder): PrayerElementDto {
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
                "title" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Title.serializer(), element)
                }

                "heading" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Heading.serializer(), element)
                }

                "subheading" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Subheading.serializer(), element)
                }

                "prose" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Prose.serializer(), element)
                }

                "song" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Song.serializer(), element)
                }

                "subtext" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Subtext.serializer(), element)
                }

                "source" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Source.serializer(), element)
                }

                "button" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Button.serializer(), element)
                }

                // Complex types
                "collapsible-block" -> {
                    json.decodeFromJsonElement(PrayerElementDto.CollapsibleBlock.serializer(), element)
                }

                "link" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Link.serializer(), element)
                }

                "link-collapsible" -> {
                    json.decodeFromJsonElement(PrayerElementDto.LinkCollapsible.serializer(), element)
                }

                "dynamic-song" -> {
                    json.decodeFromJsonElement(PrayerElementDto.DynamicSong.serializer(), element)
                }

                "dynamic-songs-block" -> {
                    json.decodeFromJsonElement(PrayerElementDto.DynamicSongsBlock.serializer(), element)
                }

                "alternative-prayers-block" -> {
                    json.decodeFromJsonElement(PrayerElementDto.AlternativePrayersBlock.serializer(), element)
                }

                "alternative-option" -> {
                    json.decodeFromJsonElement(PrayerElementDto.AlternativeOption.serializer(), element)
                }

                // If you explicitly serialize PrayerElement.Error in your JSON, handle it:
                "error" -> {
                    json.decodeFromJsonElement(PrayerElementDto.Error.serializer(), element)
                }

                // --- Fallback for unknown types ---
                else -> {
                    // Create an Error element with informative content
                    PrayerElementDto.Error("Unknown or invalid PrayerElement type: '$type'.")
                }
            }
        } catch (_: Exception) {
            // Catch any serialization exceptions that might occur even for known types
            // (e.g., missing required fields, type mismatch for a property)
//            Log.d("PrayerElementSerializer", "Error parsing PrayerElement: $element")
            PrayerElementDto.Error("Error parsing PrayerElement: ${element.toString().substring(0, 50)}")
        }
    }

    // --- Serialization part (to convert PrayerElement back to JSON) ---
    // This is less critical for your current problem (reading JSON)
    // but essential if you ever serialize PrayerElement objects back to JSON.
    override fun serialize(
        encoder: Encoder,
        value: PrayerElementDto,
    ) {
        val jsonEncoder =
            encoder as? JsonEncoder
                ?: throw SerializationException("This serializer can only be used with JsonEncoder")

        val (type, payload) =
            when (value) {
                is PrayerElementDto.Title -> {
                    "title" to json.encodeToJsonElement(PrayerElementDto.Title.serializer(), value).jsonObject
                }

                is PrayerElementDto.Heading -> {
                    "heading" to json.encodeToJsonElement(PrayerElementDto.Heading.serializer(), value).jsonObject
                }

                is PrayerElementDto.Subheading -> {
                    "subheading" to json.encodeToJsonElement(PrayerElementDto.Subheading.serializer(), value).jsonObject
                }

                is PrayerElementDto.Prose -> {
                    "prose" to json.encodeToJsonElement(PrayerElementDto.Prose.serializer(), value).jsonObject
                }

                is PrayerElementDto.Song -> {
                    "song" to json.encodeToJsonElement(PrayerElementDto.Song.serializer(), value).jsonObject
                }

                is PrayerElementDto.Subtext -> {
                    "subtext" to json.encodeToJsonElement(PrayerElementDto.Subtext.serializer(), value).jsonObject
                }

                is PrayerElementDto.Source -> {
                    "source" to json.encodeToJsonElement(PrayerElementDto.Source.serializer(), value).jsonObject
                }

                is PrayerElementDto.Button -> {
                    "button" to json.encodeToJsonElement(PrayerElementDto.Button.serializer(), value).jsonObject
                }

                is PrayerElementDto.CollapsibleBlock -> {
                    "collapsible-block" to json.encodeToJsonElement(PrayerElementDto.CollapsibleBlock.serializer(), value).jsonObject
                }

                is PrayerElementDto.Link -> {
                    "link" to json.encodeToJsonElement(PrayerElementDto.Link.serializer(), value).jsonObject
                }

                is PrayerElementDto.LinkCollapsible -> {
                    "link-collapsible" to json.encodeToJsonElement(PrayerElementDto.LinkCollapsible.serializer(), value).jsonObject
                }

                is PrayerElementDto.DynamicSong -> {
                    "dynamic-song" to json.encodeToJsonElement(PrayerElementDto.DynamicSong.serializer(), value).jsonObject
                }

                is PrayerElementDto.DynamicSongsBlock -> {
                    "dynamic-songs-block" to json.encodeToJsonElement(PrayerElementDto.DynamicSongsBlock.serializer(), value).jsonObject
                }

                is PrayerElementDto.AlternativePrayersBlock -> {
                    "alternative-prayers-block" to
                        json
                            .encodeToJsonElement(
                                PrayerElementDto.AlternativePrayersBlock.serializer(),
                                value,
                            ).jsonObject
                }

                is PrayerElementDto.AlternativeOption -> {
                    "alternative-option" to json.encodeToJsonElement(PrayerElementDto.AlternativeOption.serializer(), value).jsonObject
                }

                is PrayerElementDto.Error -> {
                    "error" to json.encodeToJsonElement(PrayerElementDto.Error.serializer(), value).jsonObject
                }
            }

        val jsonElement =
            buildJsonObject {
                put("type", type)
                payload.forEach { (key, jsonValue) ->
                    if (key != "type") put(key, jsonValue)
                }
            }

        jsonEncoder.encodeJsonElement(jsonElement)
    }
}