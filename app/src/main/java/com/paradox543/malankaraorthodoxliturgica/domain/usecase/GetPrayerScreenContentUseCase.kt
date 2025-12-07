package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerLinkDepthExceededException
import com.paradox543.malankaraorthodoxliturgica.domain.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.AppLanguage
import javax.inject.Inject

/**
 * Use-case that loads a prayer file and resolves any Link/LinkCollapsible/Dynamic blocks
 * into a fully-resolved list of PrayerElementDomain items suitable for rendering.
 */
class GetPrayerScreenContentUseCase @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val getDynamicSongsUseCase: GetDynamicSongsUseCase,
) {
    private val maxLinkDepth = 5

    suspend operator fun invoke(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int = 0,
    ): List<PrayerElementDomain> {
        if (currentDepth > maxLinkDepth) {
            throw PrayerLinkDepthExceededException(
                "Exceeded maximum link depth ($maxLinkDepth) while loading ${language.code}/$fileName",
            )
        }
        val rawElements = prayerRepository.loadPrayerElements(fileName, language)
        return resolveList(rawElements, language, currentDepth)
    }

    private suspend fun resolveList(
        list: List<PrayerElementDomain>,
        language: AppLanguage,
        currentDepth: Int,
    ): List<PrayerElementDomain> {
        val out = mutableListOf<PrayerElementDomain>()

        for (element in list) {
            when (element) {
                is PrayerElementDomain.Link -> {
                    try {
                        val loaded = prayerRepository.loadPrayerElements(element.file, language)
                        val resolved = resolveList(loaded, language, currentDepth + 1)
                        out.addAll(resolved)
                    } catch (t: Throwable) {
                        out.add(PrayerElementDomain.Error(t.message ?: "Error loading ${element.file}"))
                    }
                }

                is PrayerElementDomain.LinkCollapsible -> {
                    try {
                        val loaded = prayerRepository.loadPrayerElements(element.file, language)
                        val resolved = resolveList(loaded, language, currentDepth + 1)

                        var title: String? = null
                        val items = mutableListOf<PrayerElementDomain>()

                        for (r in resolved) {
                            when (r) {
                                is PrayerElementDomain.Title -> if (title == null) title = r.content
                                is PrayerElementDomain.Heading -> if (title == null) title = r.content
                                else -> items.add(r)
                            }
                        }

                        if (items.isEmpty()) {
                            out.add(PrayerElementDomain.Error("Linked file ${element.file} contained no displayable items"))
                        } else {
                            out.add(PrayerElementDomain.CollapsibleBlock(title ?: "Expandable Block", items))
                        }
                    } catch (t: Throwable) {
                        out.add(PrayerElementDomain.Error(t.message ?: "Error loading ${element.file}"))
                    }
                }

                is PrayerElementDomain.CollapsibleBlock -> {
                    val resolvedItems = resolveList(element.items, language, currentDepth)
                    out.add(PrayerElementDomain.CollapsibleBlock(element.title, resolvedItems))
                }

                is PrayerElementDomain.DynamicSongsBlock -> {
                    val resolved = getDynamicSongsUseCase(language, element, currentDepth)
                    out.add(resolved)
                }

                is PrayerElementDomain.DynamicSong -> {
                    val resolvedItems = resolveList(element.items, language, currentDepth)
                    out.add(
                        PrayerElementDomain.DynamicSong(
                            eventKey = element.eventKey,
                            eventTitle = element.eventTitle,
                            timeKey = element.timeKey,
                            items = resolvedItems,
                        ),
                    )
                }

                is PrayerElementDomain.AlternativePrayersBlock -> {
                    val resolvedOptions =
                        element.options
                            .map { opt ->
                                val resolvedItems = resolveList(opt.items, language, currentDepth)
                                PrayerElementDomain.AlternativeOption(opt.label, resolvedItems)
                            }
                    out.add(PrayerElementDomain.AlternativePrayersBlock(element.title, resolvedOptions))
                }

                else -> {
                    // Title, Heading, Prose, Song, Subtext, Source, Button, Error etc.
                    out.add(element)
                }
            }
        }

        return out
    }
}
