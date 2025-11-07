package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElementData

interface PrayerRepository {
    fun loadTranslations(language: AppLanguage): Map<String, String>

    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int = 0,
    ): List<PrayerElementData>

    suspend fun getSongKeyPriority(): String
}