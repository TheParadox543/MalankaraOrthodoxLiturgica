package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.PageNodeDomain

interface PrayerRepository {
    suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
    ): List<PrayerElementDomain>

    suspend fun getPrayerNavigationTree(targetLanguage: AppLanguage): PageNodeDomain
}
