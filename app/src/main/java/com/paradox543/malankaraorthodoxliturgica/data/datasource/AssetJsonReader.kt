package com.paradox543.malankaraorthodoxliturgica.data.datasource

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetJsonReader @Inject constructor(
    @param:ApplicationContext val context: Context,
    val json: Json,
) {
    inline fun <reified T> loadJsonAsset(path: String): T? =
        try {
            val jsonString =
                context.assets
                    .open(path)
                    .bufferedReader()
                    .use { it.readText() }

            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            Log.e("AssetJsonReader", "Error loading $path", e)
            null
        }
}
