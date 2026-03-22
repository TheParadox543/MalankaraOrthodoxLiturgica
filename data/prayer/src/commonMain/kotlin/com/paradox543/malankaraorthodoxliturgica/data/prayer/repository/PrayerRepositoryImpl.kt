package com.paradox543.malankaraorthodoxliturgica.data.prayer.repository

import com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping.toDomainList
import com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping.toPageNodeDomain
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

class PrayerRepositoryImpl(
    private val prayerSource: PrayerSource,
) : PrayerRepository {
    override suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
    ): List<PrayerElement> =
        prayerSource
            .loadPrayerElements(
                fileName,
                language,
            ).toDomainList()

    override suspend fun getPrayerNavigationTree(targetLanguage: AppLanguage): PageNode =
        prayerSource.loadPrayerNavigationTree(targetLanguage).toPageNodeDomain()
}