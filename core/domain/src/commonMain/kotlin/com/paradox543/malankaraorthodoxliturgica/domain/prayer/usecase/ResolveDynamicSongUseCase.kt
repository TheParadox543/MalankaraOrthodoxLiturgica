package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

/**
 * Use case responsible for resolving a special songs key into a
 * PrayerElement.DynamicSong by loading the corresponding song elements.
 */
class ResolveDynamicSongUseCase(
    private val prayerRepository: PrayerRepository,
) {
    suspend operator fun invoke(
        language: AppLanguage,
        specialSongsKey: String,
        eventTitle: String,
        timeKey: String,
    ): PrayerElement.DynamicSong? {
        val specialSongsKey = specialSongsKey
        val filename = "sacraments/qurbana/qurbanaSongs/${specialSongsKey.removeSuffix("Songs")}/$timeKey.json"

        val songElements = try {
            prayerRepository.loadPrayerElements(filename, language)
        } catch (t: Throwable) {
            emptyList()
        }

        if (songElements.isEmpty()) return null

        val title = eventTitle.ifBlank { specialSongsKey }

        return PrayerElement.DynamicSong(
            eventKey = specialSongsKey,
            eventTitle = title,
            timeKey = timeKey,
            items = songElements,
        )
    }
}
