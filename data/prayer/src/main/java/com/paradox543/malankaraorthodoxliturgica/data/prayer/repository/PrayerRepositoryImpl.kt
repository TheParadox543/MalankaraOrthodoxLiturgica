package com.paradox543.malankaraorthodoxliturgica.data.prayer.repository

import com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping.toDomainList
import com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping.toPageNodeDomain
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepositoryImpl @Inject constructor(
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