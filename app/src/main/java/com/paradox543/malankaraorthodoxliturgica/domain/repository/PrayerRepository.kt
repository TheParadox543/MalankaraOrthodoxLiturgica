package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

interface PrayerRepository {
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
    ): List<PrayerElementDomain>

    suspend fun getPrayerNavigationTree(targetLanguage: AppLanguage): PageNodeDomain
}
