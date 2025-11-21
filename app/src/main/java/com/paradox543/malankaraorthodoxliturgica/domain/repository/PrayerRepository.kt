package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain

interface PrayerRepository {
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int = 0,
    ): List<PrayerElementDomain>
}