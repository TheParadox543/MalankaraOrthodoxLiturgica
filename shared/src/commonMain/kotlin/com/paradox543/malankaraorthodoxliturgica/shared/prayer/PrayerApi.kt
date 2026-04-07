package com.paradox543.malankaraorthodoxliturgica.shared.prayer

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import org.koin.core.component.KoinComponent

class PrayerApi(
    private val repository: PrayerRepository,
) {
    suspend fun loadPrayer(fileName: String): List<PrayerUiElement> {
        val elements =
            repository.loadPrayerElements(
                fileName = fileName,
                language = AppLanguage.MALAYALAM,
            )

        return elements.map { it.toUi() }
    }
}