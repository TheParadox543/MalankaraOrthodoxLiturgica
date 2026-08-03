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
    private val resolveDynamicSongUseCase: ResolveDynamicSongUseCase,
) {
    suspend operator fun invoke(
        language: AppLanguage,
        dynamicSongsBlock: PrayerElement.DynamicSongsBlock,
        activeEventKeys: Set<String>,
        currentDepth: Int = 0,
    ): PrayerElement.DynamicSongsBlock {
        val resolvedList = mutableListOf<PrayerElement.DynamicSong>()

        // Handle default content (maybe a link which needs to be resolved)
        val defaultContent = dynamicSongsBlock.defaultContent
        if (defaultContent != null) {
            val firstItem = defaultContent.items.firstOrNull()
            if (firstItem is PrayerElement.Link) {
                // Load the linked file and replace items
                val file = firstItem.file
                val loadedItems = prayerRepository.loadPrayerElements(file, language)
                val newDynamicSong = defaultContent.copy(items = loadedItems)
                resolvedList.add(newDynamicSong)
            } else {
                resolvedList.add(defaultContent)
            }
        }

        // Add songs for provided active event keys
        for (specialSongsKey in activeEventKeys) {
            val resolvedSong = resolveDynamicSongUseCase(language, specialSongsKey, "", dynamicSongsBlock.timeKey)
            if (resolvedSong != null) {
                resolvedList.add(resolvedSong)
            }
        }

        // Add prayers for the departed at the end if not already added
        if (resolvedList.none { it.eventKey == "allDepartedFaithfulSongs" }) {
            val departedEvents = calendarRepository.getEvents(listOf("allDepartedFaithful"))
            val departedEvent = departedEvents.firstOrNull()
            if (departedEvent != null) {
                val title = when (language) {
                    AppLanguage.MALAYALAM -> departedEvent.title.ml ?: departedEvent.title.en
                    AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> departedEvent.title.en
                }
                val resolvedDeparted = resolveDynamicSongUseCase(
                    language,
                    departedEvent.specialSongsKey ?: "allDepartedFaithfulSongs",
                    title,
                    dynamicSongsBlock.timeKey
                )
                if (resolvedDeparted != null) {
                    resolvedList.add(resolvedDeparted)
                }
            }
        }
        return dynamicSongsBlock.copy(items = resolvedList)
    }
}
