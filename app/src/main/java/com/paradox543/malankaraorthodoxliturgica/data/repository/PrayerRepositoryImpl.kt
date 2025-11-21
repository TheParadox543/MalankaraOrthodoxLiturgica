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
}
