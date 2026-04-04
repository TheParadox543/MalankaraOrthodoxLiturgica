package com.paradox543.malankaraorthodoxliturgica.data.translations.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import kotlinx.serialization.json.Json

interface RawTranslationsSource {
    suspend fun loadRawTranslations(): Map<String, Map<String, String>>
}

class TranslationSource(
    private val reader: ResourceTextReader,
    private val json: Json,
) : RawTranslationsSource {
    override suspend fun loadRawTranslations(): Map<String, Map<String, String>> {
        val jsonString = reader.readText("translations.json")
        return json.decodeFromString<Map<String, Map<String, String>>>(jsonString)
    }
}