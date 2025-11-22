package com.paradox543.malankaraorthodoxliturgica.data.datasource

import android.content.Context
import android.util.Log
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNodeData
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElementData
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerContentNotFoundException
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerLinkDepthExceededException
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okio.IOException
import javax.inject.Inject

class PrayerSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) {
    private val maxLinkDepth = 5

    /**
     * Main function to load a list of PrayerElements from a JSON file.
     * NOTE: This function now only reads and parses the file and returns the raw elements.
     * Any resolution of `Link`, `LinkCollapsible`, or nested content should be done in a use-case
     * that calls this data-source.
     */
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int = 0,
    ): List<PrayerElementData> =
        withContext(Dispatchers.IO) {
            if (currentDepth > maxLinkDepth) {
                throw PrayerLinkDepthExceededException(
                    "Exceeded maximum link depth ($maxLinkDepth) while loading ${language.code}/$fileName",
                )
            }

            val filePath = "${language.code}/prayers/$fileName"
            return@withContext try {
                val inputStream = context.assets.open(filePath)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                json.decodeFromString<List<PrayerElementData>>(jsonString)
            } catch (_: IOException) {
                throw PrayerContentNotFoundException("Error loading file: $filePath.")
            } catch (_: Exception) {
                throw PrayerParsingException("Error parsing JSON in: $filePath.")
            }
        }

    suspend fun loadPrayerNavigationTree(language: AppLanguage): PageNodeData =
        withContext(Dispatchers.IO) {
            val jsonString =
                context
                    .assets
                    .open("${language.code}/prayers_tree.json")
                    .bufferedReader()
                    .use { it.readText() }
            return@withContext json.decodeFromString<PageNodeData>(jsonString)
        }
}
