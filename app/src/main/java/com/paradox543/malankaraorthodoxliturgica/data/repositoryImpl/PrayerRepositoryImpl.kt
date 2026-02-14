package com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl

import com.paradox543.malankaraorthodoxliturgica.data.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toDomainList
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.repository.PrayerRepository
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
    ): List<PrayerElementDomain> =
        prayerSource
            .loadPrayerElements(
                fileName,
                language,
            ).toDomainList()

    override suspend fun getPrayerNavigationTree(targetLanguage: AppLanguage): PageNodeDomain =
        prayerSource.loadPrayerNavigationTree(targetLanguage).toDomain()
}
