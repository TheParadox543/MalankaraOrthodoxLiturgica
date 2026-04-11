package com.paradox543.malankaraorthodoxliturgica.data.bible.datasource

import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleChapterDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceTemplatesDto
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import kotlinx.serialization.json.Json

class BibleSource(
    private val reader: ResourceTextReader,
    private val json: Json,
) {
    private suspend inline fun <reified T> readJson(path: String): T {
        val jsonString =
            try {
                reader.readText(path)
            } catch (t: Throwable) {
                throw AssetReadException("Failed to read asset at path: $path", t)
            }

        return try {
            json.decodeFromString<T>(jsonString)
        } catch (t: Throwable) {
            throw AssetParsingException("Failed to parse asset at path: $path", t)
        }
    }

    suspend fun readBibleDetails(): List<BibleBookDetailsDto> = readJson("bibleBookMetadata.json")

    suspend fun readPrefaceTemplates(): PrefaceTemplatesDto = readJson("bible_preface_templates.json")

    suspend fun readBibleChapter(path: String): BibleChapterDto = readJson(path)
}