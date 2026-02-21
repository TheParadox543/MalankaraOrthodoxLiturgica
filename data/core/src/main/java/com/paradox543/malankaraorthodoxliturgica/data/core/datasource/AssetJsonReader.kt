package com.paradox543.malankaraorthodoxliturgica.data.core.datasource

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetJsonReader @Inject constructor(
    @param:ApplicationContext val context: Context,
    val json: Json,
) {
    inline fun <reified T> loadJsonAsset(path: String): T {
        val jsonString =
            try {
                context.assets
                    .open(path)
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e: Exception) {
                throw AssetReadException(
                    "Failed to read asset at path: $path",
                    e,
                )
            }

        return try {
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            throw AssetParsingException(
                "Failed to parse asset at path: $path",
                e,
            )
        }
    }
}