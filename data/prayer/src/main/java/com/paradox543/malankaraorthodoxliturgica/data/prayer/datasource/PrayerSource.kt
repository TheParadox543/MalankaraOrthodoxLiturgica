package com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PageNodeDto
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerContentNotFoundException
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerParsingException
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PrayerSource @Inject constructor(
    private val reader: AssetJsonReader,
) {
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
    ): List<PrayerElementDto> =
        withContext(Dispatchers.IO) {
            val filePath = "${language.code}/prayers/$fileName"
            try {
                reader.loadJsonAsset<List<PrayerElementDto>>(filePath)
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
    suspend fun loadPrayerNavigationTree(language: AppLanguage): PageNodeDto =
        withContext(Dispatchers.IO) {
            val filename = "${language.code}/prayers_tree.json"
            try {
                reader.loadJsonAsset<PageNodeDto>(filename)
            } catch (e: AssetReadException) {
                throw PrayerContentNotFoundException("Prayer navigation tree not found: $filename", e)
            } catch (e: AssetParsingException) {
                throw PrayerContentNotFoundException("Prayer navigation tree malformed: $filename", e)
            }
        }
}