package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

/**
 * Use case responsible for resolving a DynamicSongsBlock into a fully populated
 * DynamicSongsBlock by querying the calendar for special song keys and loading
 * the corresponding prayer/song elements via the PrayerRepository.
 */
class GetDynamicSongsUseCase(
    private val prayerRepository: PrayerRepository,
    private val calendarRepository: CalendarRepository,
) {
    suspend operator fun invoke(
        language: AppLanguage,
        dynamicSongsBlock: PrayerElement.DynamicSongsBlock,
        currentDepth: Int = 0,
    ): PrayerElement.DynamicSongsBlock {
        val resolvedBlock = dynamicSongsBlock.copy(items = dynamicSongsBlock.items.toMutableList())

        // Handle default content (may be a link which needs to be resolved)
        val defaultContent = dynamicSongsBlock.defaultContent
        if (defaultContent != null) {
            val firstItem = defaultContent.items.firstOrNull()
            if (firstItem is PrayerElement.Link) {
                // Load the linked file and replace items
                val file = firstItem.file
                val loadedItems = prayerRepository.loadPrayerElements(file, language)
                val newDynamicSong = defaultContent.copy(items = loadedItems)
                resolvedBlock.items.add(newDynamicSong)
            } else {
                resolvedBlock.items.add(defaultContent)
            }
        }

        // Add songs for upcoming week events
        val weekEventItems = calendarRepository.getUpcomingWeekEventItems()
        for (event in weekEventItems) {
            val specialSongsKey = event.specialSongsKey
            if (specialSongsKey != null) {
                val filename = "qurbanaSongs/${specialSongsKey.removeSuffix("Songs")}/${dynamicSongsBlock.timeKey}.json"
                val songElements =
                    try {
                        prayerRepository.loadPrayerElements(filename, language)
                    } catch (t: Throwable) {
                        emptyList()
                    }

                if (songElements.isEmpty()) continue

                val title =
                    when (language) {
                        AppLanguage.MALAYALAM -> event.title.ml ?: event.title.en
                        AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> event.title.en
                    }

                resolvedBlock.items.add(
                    PrayerElement.DynamicSong(
                        eventKey = specialSongsKey,
                        eventTitle = title,
                        timeKey = dynamicSongsBlock.timeKey,
                        items = songElements,
                    ),
                )
            }
        }

        // Add prayers for the departed at the end if not already added
        if (resolvedBlock.items.none { it.eventKey == "allDepartedFaithful" }) {
            val departedFilename = "qurbanaSongs/allDepartedFaithful/${dynamicSongsBlock.timeKey}.json"
            val departedSongElements =
                try {
                    prayerRepository.loadPrayerElements(departedFilename, language)
                } catch (t: Throwable) {
                    emptyList()
                }

            if (departedSongElements.isNotEmpty()) {
                val title =
                    when (language) {
                        AppLanguage.MALAYALAM -> "സകല വാങ്ങിപ്പോയവരുടെയും ഞായറാഴ്\u200Cച"
                        AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> "All Departed Faithful"
                    }
                resolvedBlock.items.add(
                    PrayerElement.DynamicSong(
                        eventKey = "allDepartedFaithful",
                        eventTitle = title,
                        timeKey = dynamicSongsBlock.timeKey,
                        items = departedSongElements,
                    ),
                )
            }
        }

        return resolvedBlock
    }
}
