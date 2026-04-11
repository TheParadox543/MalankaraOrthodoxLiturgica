package com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PageNodeDto
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerContentNotFoundException
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerParsingException
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.serialization.json.Json

class PrayerSource(
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

    /**
     * Main function to load a list of PrayerElements from a JSON file.
     * NOTE: This function now only reads and parses the file and returns the raw elements.
     * Any resolution of `Link`, `LinkCollapsible`, or nested content should be done in a use-case
     * that calls this data-source.
     *
     * Throws [PrayerParsingException] if the asset cannot be read or parsed.
     */
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
    ): List<PrayerElementDto> {
        val filePath = "${language.code}/prayers/$fileName"
        return try {
            readJson(filePath)
        } catch (e: AssetReadException) {
            throw PrayerParsingException("Error reading prayer file: $filePath.", e)
        } catch (e: AssetParsingException) {
            throw PrayerParsingException("Error parsing JSON in: $filePath.", e)
        }
    }

    /**
     * Loads the prayer navigation tree for the given language.
     *
     * Throws [PrayerContentNotFoundException] if the asset cannot be read or parsed.
     */
    suspend fun loadPrayerNavigationTree(language: AppLanguage): PageNodeDto {
        val filename = "${language.code}/prayers_tree.json"
        return try {
            readJson(filename)
        } catch (e: AssetReadException) {
            throw PrayerContentNotFoundException("Prayer navigation tree not found: $filename", e)
        } catch (e: AssetParsingException) {
            throw PrayerContentNotFoundException("Prayer navigation tree malformed: $filename", e)
        }
    }
}