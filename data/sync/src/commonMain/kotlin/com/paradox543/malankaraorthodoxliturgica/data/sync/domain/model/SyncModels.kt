package com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObject.Companion.serializer
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = RootManifestSerializer::class)
data class RootManifest(
    val schemaVersion: Int,
    val generatedAt: String,
    val domains: Map<String, DomainManifestInfo>,
)

object RootManifestSerializer : KSerializer<RootManifest> {
    override val descriptor: SerialDescriptor = serializer().descriptor

    override fun deserialize(decoder: Decoder): RootManifest {
        val jsonObject = decoder.decodeSerializableValue(serializer())
        val schemaVersion = jsonObject["schemaVersion"]?.jsonPrimitive?.int ?: 0
        val generatedAt = jsonObject["generatedAt"]?.jsonPrimitive?.content ?: ""

        val domains =
            jsonObject
                .filterKeys { it != "schemaVersion" && it != "generatedAt" }
                .mapValues { (_, value) ->
                    decoder.json.decodeFromJsonElement<DomainManifestInfo>(value)
                }

        return RootManifest(schemaVersion, generatedAt, domains)
    }

    override fun serialize(
        encoder: Encoder,
        value: RootManifest,
    ) {
        val map = mutableMapOf<String, JsonElement>()
        map["schemaVersion"] = JsonPrimitive(value.schemaVersion)
        map["generatedAt"] = JsonPrimitive(value.generatedAt)
        value.domains.forEach { (k, v) ->
            map[k] = encoder.json.encodeToJsonElement(DomainManifestInfo.serializer(), v)
        }
        encoder.encodeSerializableValue(serializer(), JsonObject(map))
    }
}

val Decoder.json: Json get() = (this as? JsonDecoder)?.json ?: Json.Default
val Encoder.json: Json get() = (this as? JsonEncoder)?.json ?: Json.Default

@Serializable
data class DomainManifestInfo(
    val schemaVersion: Int,
    val contentVersion: Int,
    val manifest: String,
)

@Serializable
data class DomainManifest(
    val domain: String,
    val contentVersion: Int,
    val files: List<FileEntry>,
)

@Serializable
data class FileEntry(
    val name: String,
    val location: String,
    val checksum: String,
    val sizeBytes: Long,
)

enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    FAILURE,
}

data class SyncState(
    val status: SyncStatus,
    val progress: Float = 0f,
    val hasUpdate: Boolean = false,
    val error: Throwable? = null,
)
