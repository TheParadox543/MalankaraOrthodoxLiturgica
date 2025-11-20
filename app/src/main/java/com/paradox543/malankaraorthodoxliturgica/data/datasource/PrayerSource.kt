package com.paradox543.malankaraorthodoxliturgica.data.datasource

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerContentNotFoundException
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElementData
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerLinkDepthExceededException
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerParsingException
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.utils.applyPrayerReplacements
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import okio.IOException
import org.json.JSONObject
import javax.inject.Inject

class PrayerSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) {
    private val maxLinkDepth = 5

    private suspend fun loadAlternativePrayers(
        language: AppLanguage,
        alternativePrayersBlock: PrayerElementData.AlternativePrayersBlock,
        currentDepth: Int,
    ): PrayerElementData.AlternativePrayersBlock {
        val updatedOptions =
            alternativePrayersBlock.options.map { option ->
                val firstItem = option.items.firstOrNull()
                if (firstItem is PrayerElementData.Link) {
                    val content =
                        loadPrayerElements(
                            firstItem.file,
                            language,
                            currentDepth + 1,
                        )
                    option.copy(items = content)
                } else {
                    option
                }
            }
        return alternativePrayersBlock.copy(options = updatedOptions)
    }

    /**
     * Main function to load and resolve a list of PrayerElements from a JSON file.
     * Handles recursive loading of 'link' and processing of 'link-collapsible'.
     */
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int = 0,
    ): List<PrayerElementData> {
        if (currentDepth > maxLinkDepth) {
            throw PrayerLinkDepthExceededException(
                "Exceeded maximum link depth ($maxLinkDepth) while loading ${language.code}/$fileName",
            )
        }

        val rawElements: List<PrayerElementData> =
            try {
                val inputStream =
                    context.assets.open("prayers/${language.code}/$fileName")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                json.decodeFromString<List<PrayerElementData>>(jsonString) // Use the injected 'json'
            } catch (_: IOException) {
                throw PrayerContentNotFoundException("Error loading file: ${language.code}/$fileName.")
            } catch (_: Exception) {
                throw PrayerParsingException("Error parsing JSON in: ${language.code}/$fileName.")
            }

        val resolvedElements = mutableListOf<PrayerElementData>()
        for (element in rawElements) {
            when (element) {
                is PrayerElementData.Title -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElementData.Heading -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElementData.Subheading -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElementData.Prose -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElementData.Song -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElementData.Subtext -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))
                is PrayerElementData.Source -> resolvedElements.add(element.copy(content = element.content.applyPrayerReplacements()))

                is PrayerElementData.Link -> {
                    try {
                        resolvedElements.addAll(
                            loadPrayerElements(element.file, language, currentDepth + 1),
                        )
                    } catch (_: PrayerContentNotFoundException) {
                        resolvedElements.add(PrayerElementData.Error("Failed to find linked file: ${language.code}/${element.file}."))
                    } catch (_: Exception) {
                        resolvedElements.add(PrayerElementData.Error("Failed to load linked file: ${language.code}/${element.file}."))
                    }
                }

                is PrayerElementData.LinkCollapsible -> {
                    try {
                        resolvedElements.add(
                            loadPrayerAsCollapsibleBlock(
                                element.file,
                                language,
                                currentDepth + 1, // Increment depth as we're loading a new file
                            ),
                        )
                    } catch (_: PrayerContentNotFoundException) {
                        resolvedElements.add(PrayerElementData.Error("Failed to find collapsible link: ${language.code}/${element.file}."))
                    } catch (_: Exception) {
                        resolvedElements.add(PrayerElementData.Error("Failed to load collapsible link: ${language.code}/${element.file}."))
                    }
                }

                is PrayerElementData.CollapsibleBlock -> {
                    val resolvedItems = mutableListOf<PrayerElementData>()
                    element.items.forEach { nestedItem ->
                        when (nestedItem) {
                            is PrayerElementData.Link -> { // Handle nested links
                                try {
                                    resolvedItems.addAll(
                                        loadPrayerElements(
                                            nestedItem.file,
                                            language,
                                            currentDepth + 1,
                                        ),
                                    )
                                } catch (_: PrayerContentNotFoundException) {
                                    resolvedElements
                                        .add(PrayerElementData.Error("Failed to find nested link: ${language.code}/${nestedItem.file}."))
                                } catch (_: Exception) {
                                    resolvedItems.add(PrayerElementData.Error("Failed to load nested link: ${nestedItem.file}."))
                                }
                            }

                            else -> resolvedItems.add(nestedItem)
                        }
                    }
                    resolvedElements.add(element.copy(items = resolvedItems))
                }

                is PrayerElementData.DynamicSongsBlock -> {
                    resolvedElements.add(
//                        loadDynamicSongs(
//                            language,
//                            element,
//                            currentDepth
//                        )
                        element,
                    )
                }

                is PrayerElementData.AlternativePrayersBlock -> {
                    resolvedElements.add(
                        loadAlternativePrayers(
                            language,
                            element,
                            currentDepth,
                        ),
                    )
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
    ): PrayerElementData.CollapsibleBlock {
        // Load the elements of the target file using the main loader
        val elementsOfLinkedFile =
            loadPrayerElements(fileName, language, currentDepth)

        var collapsibleBlockTitle: String? = null
        val itemsForCollapsibleBlock = mutableListOf<PrayerElementData>()

        // Process the elements to find a title and collect other items
        for (element in elementsOfLinkedFile) {
            when (element) {
                is PrayerElementData.Title -> {
                    if (collapsibleBlockTitle == null) { // Only update if title hasn't been found from Heading
                        collapsibleBlockTitle = element.content
                    }
                    // This element is consumed as the title, so don't add to items
                }

                is PrayerElementData.Heading -> {
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

        return PrayerElementData.CollapsibleBlock(
            title = collapsibleBlockTitle ?: "Expandable Block",
            items = itemsForCollapsibleBlock,
        )
    }
}
