package com.paradox543.malankaraorthodoxliturgica.data.core.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.data.core.platform.PlatformAssetReader
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

class AssetJsonReader(
    private val platformAssetReader: PlatformAssetReader,
    private val json: Json,
) {
    fun <T> loadJsonAsset(
        path: String,
        deserializer: DeserializationStrategy<T>,
    ): T {
        val jsonString =
            try {
                platformAssetReader.readText(path)
            } catch (t: Throwable) {
                throw AssetReadException("Failed to read asset at path: $path", t)
            }

        return try {
            json.decodeFromString(deserializer, jsonString)
        } catch (t: Throwable) {
            throw AssetParsingException("Failed to parse asset at path: $path", t)
        }
    }

    inline fun <reified T> loadJsonAsset(path: String): T = loadJsonAsset(path, serializer())
}