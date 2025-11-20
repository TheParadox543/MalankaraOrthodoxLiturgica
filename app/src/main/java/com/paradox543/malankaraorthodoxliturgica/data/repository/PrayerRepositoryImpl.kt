package com.paradox543.malankaraorthodoxliturgica.data.repository

import com.paradox543.malankaraorthodoxliturgica.data.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.datasource.TranslationSource
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toDomainList
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.repository.PrayerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepositoryImpl @Inject constructor(
    private val prayerSource: PrayerSource,
    private val translationSource: TranslationSource,
    private val calendarRepository: CalendarRepositoryImpl,
) : PrayerRepository {
    override fun loadTranslations(language: AppLanguage): Map<String, String> = translationSource.loadTranslations(language)

    override suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int,
    ): List<PrayerElementDomain> =
        prayerSource
            .loadPrayerElements(
                fileName,
                language,
                currentDepth,
            ).toDomainList()

    override suspend fun getSongKeyPriority(): String {
        calendarRepository.initialize()
        val weekEventItems = calendarRepository.getUpcomingWeekEventItems()
        for (item in weekEventItems) {
            if (item.specialSongsKey != null) {
                return item.specialSongsKey
            }
        }
        return "default"
    }

//    private suspend fun loadDynamicSongs(
//        language: AppLanguage,
//        dynamicSongsBlock: PrayerElementData.DynamicSongsBlock,
//        currentDepth: Int,
//    ): PrayerElementData.DynamicSongsBlock {
//        calendarRepository.initialize()
//        val weekEvents = calendarRepository.getUpcomingWeekEventItems()
//
//        if (dynamicSongsBlock.defaultContent != null) {
//            val dynamicSong = dynamicSongsBlock.defaultContent
//            if (dynamicSong.items.first() is PrayerElementData.Link) {
//                val newDynamicSong =
//                    dynamicSong.copy(
//                        items =
//                            loadPrayerElementsData(
//                                this, (dynamicSong.items.first() as PrayerElementData.Link).file,
//                                language,
//                                currentDepth + 1,
//                            ),
//                    )
//                dynamicSongsBlock.items.add(newDynamicSong)
//            } else {
//                dynamicSongsBlock.items.add(dynamicSongsBlock.defaultContent)
//            }
//        }
//        weekEvents.forEach { event ->
//            if (event.specialSongsKey != null) {
//                val songElements =
//                    try {
//                        loadPrayerElementsData(
//                            this,
//                            "qurbanaSongs/${event.specialSongsKey.removeSuffix("Songs")}/${dynamicSongsBlock.timeKey}.json",
//                            language,
//                        )
//                    } catch (e: Exception) {
//                        Log.d(
//                            "PrayerRepository",
//                            "Failed to load dynamic song: ${event.specialSongsKey}/${dynamicSongsBlock.timeKey}. ${e.message}",
//                        )
//                        emptyList()
//                    }
//                if (songElements.isEmpty()) return@forEach
//                val title =
//                    when (language) {
//                        AppLanguage.MALAYALAM -> event.title.ml ?: event.title.en
//                        AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> event.title.en
//                    }
//                dynamicSongsBlock.items.add(
//                    PrayerElementData.DynamicSong(
//                        eventKey = event.specialSongsKey,
//                        eventTitle = title,
//                        timeKey = dynamicSongsBlock.timeKey,
//                        items = songElements,
//                    ),
//                )
//            }
//        }
//
//        // Adding prayers for the departed at the end
//        if (dynamicSongsBlock.items.any { it.eventKey != "allDepartedFaithful" }) {
//            val departedSongElements =
//                try {
//                    loadPrayerElementsData(
//                        this, "qurbanaSongs/allDepartedFaithful/${dynamicSongsBlock.timeKey}.json",
//                        language,
//                    )
//                } catch (_: Exception) {
//                    emptyList()
//                }
//            if (departedSongElements.isNotEmpty()) {
//                val title =
//                    when (language) {
//                        AppLanguage.MALAYALAM -> "സകല വാങ്ങിപ്പോയവരുടെയും ഞായറാഴ്\u200Cച"
//                        AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> "All Departed Faithful"
//                    }
//                dynamicSongsBlock.items.add(
//                    PrayerElementData.DynamicSong(
//                        "allDepartedFaithful",
//                        title,
//                        dynamicSongsBlock.timeKey,
//                        departedSongElements,
//                    ),
//                )
//            }
//        }
//        return dynamicSongsBlock
//    }
}
