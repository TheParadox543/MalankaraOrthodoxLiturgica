package com.paradox543.malankaraorthodoxliturgica.data.datasource

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerContentNotFoundException
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElementData
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerLinkDepthExceededException
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerParsingException
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int = 0,
    ): List<PrayerElementData> {
        if (currentDepth > maxLinkDepth) {
            throw PrayerLinkDepthExceededException(
                "Exceeded maximum link depth ($maxLinkDepth) while loading ${language.code}/$fileName",
            )
        }

        return try {
            val inputStream = context.assets.open("prayers/${language.code}/$fileName")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            json.decodeFromString<List<PrayerElementData>>(jsonString)
        } catch (_: IOException) {
            throw PrayerContentNotFoundException("Error loading file: ${language.code}/$fileName.")
        } catch (_: Exception) {
            throw PrayerParsingException("Error parsing JSON in: ${language.code}/$fileName.")
        }
    }
}
