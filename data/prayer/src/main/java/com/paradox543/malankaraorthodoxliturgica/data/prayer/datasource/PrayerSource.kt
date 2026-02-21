package com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
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
     */
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
    ): List<PrayerElementDto> =
        withContext(Dispatchers.IO) {
            val filePath = "${language.code}/prayers/$fileName"
            return@withContext reader.loadJsonAsset<List<PrayerElementDto>>(filePath)
                ?: throw PrayerParsingException("Error parsing JSON in: $filePath.")
        }

    suspend fun loadPrayerNavigationTree(language: AppLanguage): PageNodeDto =
        withContext(Dispatchers.IO) {
            val filename = "${language.code}/prayers_tree.json"
            return@withContext reader.loadJsonAsset<PageNodeDto>(filename)
                ?: throw PrayerContentNotFoundException("Prayer navigation tree not found: $filename")
        }
}