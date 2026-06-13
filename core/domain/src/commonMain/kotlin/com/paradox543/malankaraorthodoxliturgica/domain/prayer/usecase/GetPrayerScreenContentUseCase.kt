package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.LoadBibleReadingUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerLinkDepthExceededException
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

/**
 * Use-case that loads a prayer file and resolves any Link/LinkCollapsible/Dynamic blocks
 * into a fully-resolved list of PrayerElement items suitable for rendering.
 */
class GetPrayerScreenContentUseCase(
    private val prayerRepository: PrayerRepository,
    private val getDynamicSongsUseCase: GetDynamicSongsUseCase,
    private val loadBibleReadingUseCase: LoadBibleReadingUseCase,
) {
    private val maxLinkDepth = 5

    suspend operator fun invoke(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int = 0,
    ): List<PrayerElement> {
        if (currentDepth > maxLinkDepth) {
            throw PrayerLinkDepthExceededException(
                "Exceeded maximum link depth ($maxLinkDepth) while loading ${language.code}/$fileName",
            )
        }
        val rawElements = prayerRepository.loadPrayerElements(fileName, language)
        return resolveList(rawElements, language, currentDepth)
    }

    private suspend fun resolveList(
        list: List<PrayerElement>,
        language: AppLanguage,
        currentDepth: Int,
    ): List<PrayerElement> {
        val out = mutableListOf<PrayerElement>()

        for (element in list) {
            when (element) {
                is PrayerElement.Link -> {
                    try {
                        val loaded = prayerRepository.loadPrayerElements(element.file, language)
                        val resolved = resolveList(loaded, language, currentDepth + 1)
                        out.addAll(resolved)
                    } catch (t: Throwable) {
                        out.add(PrayerElement.Error(t.message ?: "Error loading ${element.file}"))
                    }
                }

                is PrayerElement.LinkCollapsible -> {
                    try {
                        val loaded = prayerRepository.loadPrayerElements(element.file, language)
                        val resolved = resolveList(loaded, language, currentDepth + 1)

                        var title: String? = null
                        val items = mutableListOf<PrayerElement>()

                        for (r in resolved) {
                            when (r) {
                                is PrayerElement.Title -> if (title == null) title = r.content
                                is PrayerElement.Heading -> if (title == null) title = r.content
                                else -> items.add(r)
                            }
                        }

                        if (items.isEmpty()) {
                            out.add(PrayerElement.Error("Linked file ${element.file} contained no displayable items"))
                        } else {
                            out.add(PrayerElement.CollapsibleBlock(title ?: "Expandable Block", items))
                        }
                    } catch (t: Throwable) {
                        out.add(PrayerElement.Error(t.message ?: "Error loading ${element.file}"))
                    }
                }

                is PrayerElement.CollapsibleBlock -> {
                    val resolvedItems = resolveList(element.items, language, currentDepth)
                    out.add(PrayerElement.CollapsibleBlock(element.title, resolvedItems))
                }

                is PrayerElement.DynamicSongsBlock -> {
                    val resolved = getDynamicSongsUseCase(language, element, currentDepth)
                    out.add(resolved)
                }

                is PrayerElement.DynamicSong -> {
                    val resolvedItems = resolveList(element.items, language, currentDepth)
                    out.add(
                        PrayerElement.DynamicSong(
                            eventKey = element.eventKey,
                            eventTitle = element.eventTitle,
                            timeKey = element.timeKey,
                            items = resolvedItems,
                        ),
                    )
                }

                is PrayerElement.AlternativePrayersBlock -> {
                    val resolvedOptions =
                        element.options
                            .map { opt ->
                                val resolvedItems = resolveList(opt.items, language, currentDepth)
                                PrayerElement.AlternativeOption(opt.label, resolvedItems)
                            }
                    out.add(PrayerElement.AlternativePrayersBlock(element.title, resolvedOptions))
                }

                is PrayerElement.PrayerBibleReading -> {
                    val reading = loadBibleReadingUseCase(element.references, language)
                    out.add(PrayerElement.PrayerBibleReading(references = element.references, readingContent = reading))
                }

                is PrayerElement.Title,
                is PrayerElement.Heading,
                is PrayerElement.Subheading,
                is PrayerElement.Prose,
                is PrayerElement.Song,
                is PrayerElement.Subtext,
                is PrayerElement.Source,
                is PrayerElement.Button,
                is PrayerElement.Error,
                -> {
                    out.add(element)
                }

                is PrayerElement.AlternativeOption -> {
                    out.add(
                        PrayerElement.Error(
                            "Unexpected AlternativeOption with label ${element.label} found while resolving.",
                        ),
                    )
                }
            }
        }

        return out
    }
}
