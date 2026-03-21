package com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

interface PrayerRepository {
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
    ): List<PrayerElement>

    suspend fun getPrayerNavigationTree(targetLanguage: AppLanguage): PageNode
}