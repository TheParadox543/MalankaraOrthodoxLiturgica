package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import android.util.Log
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerContentNotFoundException
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerLinkDepthExceededException
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import okio.IOException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
    private val calendarRepository: LiturgicalCalendarRepository,
) {
    fun loadTranslations(language: AppLanguage): Map<String, String> {
        val json =
            context
                .assets
                .open("translations.json")
                .bufferedReader()
                .use { it.readText() }
        val jsonObject = JSONObject(json)
        val translationMap = mutableMapOf<String, String>()
        for (key in jsonObject.keys()) {
            val innerObject = jsonObject.getJSONObject(key)
            val code =
                when (language) {
                    AppLanguage.MALAYALAM -> AppLanguage.MALAYALAM.code
                    AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> AppLanguage.ENGLISH.code
                }
            translationMap[key] = innerObject.getString(code)
        }
        return translationMap
    }

    private val maxLinkDepth = 5

    /**
     * Main function to load and resolve a list of PrayerElements from a JSON file.
     * Handles recursive loading of 'link' and processing of 'link-collapsible'.
     */
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int = 0,
    ): List<PrayerElement> {
        if (currentDepth > maxLinkDepth) {
            throw PrayerLinkDepthExceededException(
                "Exceeded maximum link depth ($maxLinkDepth) while loading ${language.code}/$fileName",
            )
        }

        val rawElements: List<PrayerElement> =
            try {
                val inputStream = context.assets.open("prayers/${language.code}/$fileName")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                json.decodeFromString<List<PrayerElement>>(jsonString) // Use the injected 'json'
            } catch (_: IOException) {
                throw PrayerContentNotFoundException("Error loading file: ${language.code}/$fileName.")
            } catch (_: Exception) {
                throw PrayerParsingException("Error parsing JSON in: ${language.code}/$fileName.")
            }

        val resolvedElements = mutableListOf<PrayerElement>()
        for (element in rawElements) {
            when (element) {
                is PrayerElement.Title -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElement.Heading -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElement.Subheading -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElement.Prose -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElement.Song -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElement.Subtext -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElement.Source -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))

                is PrayerElement.Link -> {
                    try {
                        resolvedElements.addAll(
                            loadPrayerElements(element.file, language, currentDepth + 1),
                        )
                    } catch (_: PrayerContentNotFoundException) {
                        resolvedElements.add(PrayerElement.Error("Failed to find linked file: ${language.code}/${element.file}."))
                    } catch (_: Exception) {
                        resolvedElements.add(PrayerElement.Error("Failed to load linked file: ${language.code}/${element.file}."))
                    }
                }

                is PrayerElement.LinkCollapsible -> {
                    try {
                        resolvedElements.add(
                            loadPrayerAsCollapsibleBlock(
                                element.file,
                                language,
                                currentDepth + 1, // Increment depth as we're loading a new file
                            ),
                        )
                    } catch (_: PrayerContentNotFoundException) {
                        resolvedElements.add(PrayerElement.Error("Failed to find collapsible link: ${language.code}/${element.file}."))
                    } catch (_: Exception) {
                        resolvedElements.add(PrayerElement.Error("Failed to load collapsible link: ${language.code}/${element.file}."))
                    }
                }

                is PrayerElement.CollapsibleBlock -> {
                    val resolvedItems = mutableListOf<PrayerElement>()
                    element.items.forEach { nestedItem ->
                        when (nestedItem) {
                            is PrayerElement.Link -> { // Handle nested links
                                try {
                                    resolvedItems.addAll(loadPrayerElements(nestedItem.file, language, currentDepth + 1))
                                } catch (_: PrayerContentNotFoundException) {
                                    resolvedElements
                                        .add(PrayerElement.Error("Failed to find nested link: ${language.code}/${nestedItem.file}."))
                                } catch (_: Exception) {
                                    resolvedItems.add(PrayerElement.Error("Failed to load nested link: ${nestedItem.file}."))
                                }
                            }
                            else -> resolvedItems.add(nestedItem)
                        }
                    }
                    resolvedElements.add(element.copy(items = resolvedItems))
                }

                is PrayerElement.DynamicSongsBlock -> {
                    resolvedElements.add(loadDynamicSongs(language, element))
                }

                else -> {
                    resolvedElements.add(element)
                }
            }
        }
        return resolvedElements
    }

    /**
     * Helper function to load a JSON file and transform its content into a single
     * PrayerElement.CollapsibleBlock, extracting a title from the file's content.
     * This is designed to be called specifically by PrayerElement.LinkCollapsible.
     *
     * @param fileName The JSON file to load (e.g., "litanies.json").
     * @param language The language of the file.
     * @param currentDepth The current recursion depth (passed from the caller).
     * @return A fully formed PrayerElement.CollapsibleBlock.
     * @throws PrayerContentNotFoundException if the file or its content is not found/empty.
     * @throws PrayerParsingException if the JSON is malformed.
     */
    private suspend fun loadPrayerAsCollapsibleBlock(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int,
    ): PrayerElement.CollapsibleBlock {
        // Load the elements of the target file using the main loader
        val elementsOfLinkedFile = loadPrayerElements(fileName, language, currentDepth)

        var collapsibleBlockTitle: String? = null
        val itemsForCollapsibleBlock = mutableListOf<PrayerElement>()

        // Process the elements to find a title and collect other items
        for (element in elementsOfLinkedFile) {
            when (element) {
                is PrayerElement.Title -> {
                    if (collapsibleBlockTitle == null) { // Only update if title hasn't been found from Heading
                        collapsibleBlockTitle = element.content
                    }
                    // This element is consumed as the title, so don't add to items
                }
                is PrayerElement.Heading -> {
                    if (collapsibleBlockTitle == null) { // Only update if title hasn't been found
                        collapsibleBlockTitle = element.content
                    }
                    // This element is consumed as the title, so don't add to items
                }
                else -> itemsForCollapsibleBlock.add(element)
            }
        }

        // Ensure the file had actual content
        if (itemsForCollapsibleBlock.isEmpty()) {
            throw PrayerContentNotFoundException(
                "Linked file ${language.code}/$fileName contained no valid displayable items for a collapsible block.",
            )
        }

        return PrayerElement.CollapsibleBlock(
            title = collapsibleBlockTitle ?: "Expandable Block",
            items = itemsForCollapsibleBlock,
        )
    }

    suspend fun loadDynamicSongs(
        language: AppLanguage,
        dynamicSongsBlock: PrayerElement.DynamicSongsBlock,
    ): PrayerElement.DynamicSongsBlock {
        calendarRepository.initialize()
        val weekEvents = calendarRepository.getUpcomingWeekEventItems()

        if (dynamicSongsBlock.defaultContent != null) {
            dynamicSongsBlock.items.add(dynamicSongsBlock.defaultContent)
        }
        weekEvents.forEach { event ->
            if (event.specialSongsKey != null) {
                val songElements =
                    try {
                        loadPrayerElements(
                            "qurbanaSongs/${event.specialSongsKey.removeSuffix("Songs")}/${dynamicSongsBlock.timeKey}.json",
                            language,
                        )
                    } catch (e: Exception) {
                        Log.d(
                            "PrayerRepository",
                            "Failed to load dynamic song: ${event.specialSongsKey}/${dynamicSongsBlock.timeKey}. ${e.message}",
                        )
                        emptyList()
                    }
                if (songElements.isEmpty()) return@forEach
                val title =
                    when (language) {
                        AppLanguage.MALAYALAM -> event.title.ml ?: event.title.en
                        AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> event.title.en
                    }
                dynamicSongsBlock.items.add(
                    PrayerElement.DynamicSong(
                        eventKey = event.specialSongsKey,
                        eventTitle = title,
                        timeKey = dynamicSongsBlock.timeKey,
                        items = songElements,
                    ),
                )
            }
        }

        // Adding prayers for the departed at the end
        if (dynamicSongsBlock.items.any { it.eventKey != "allDepartedFaithful" }) {
            val departedSongElements =
                try {
                    loadPrayerElements(
                        "qurbanaSongs/allDepartedFaithful/${dynamicSongsBlock.timeKey}.json",
                        language,
                    )
                } catch (_: Exception) {
                    emptyList()
                }
            if (departedSongElements.isNotEmpty()) {
                val title =
                    when (language) {
                        AppLanguage.MALAYALAM -> "സകല വാങ്ങിപ്പോയവരുടെയും ഞായറാഴ്\u200Cച"
                        AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> "All Departed Faithful"
                    }
                dynamicSongsBlock.items.add(
                    PrayerElement.DynamicSong(
                        "allDepartedFaithful",
                        title,
                        dynamicSongsBlock.timeKey,
                        departedSongElements,
                    ),
                )
            }
        }
        return dynamicSongsBlock
    }

    suspend fun getSongKeyPriority(): String {
        calendarRepository.initialize()
        val weekEventItems = calendarRepository.getUpcomingWeekEventItems()
        for (item in weekEventItems) {
            if (item.specialSongsKey != null) {
                return item.specialSongsKey
            }
        }
        return "default"
    }
}

fun String.applyPrayerReplacements(): String =
    this
        .replace("/t", "    ")
        .replace("/u200b", "\u200b")