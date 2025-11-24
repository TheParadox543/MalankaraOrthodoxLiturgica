package com.paradox543.malankaraorthodoxliturgica.data.repository

import com.paradox543.malankaraorthodoxliturgica.data.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toDomainList
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNodeData
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.PageNodeDomain
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepositoryImpl @Inject constructor(
    private val prayerSource: PrayerSource,
) : PrayerRepository {
    override suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
        currentDepth: Int,
    ): List<PrayerElementDomain> =
        prayerSource
            .loadPrayerElements(
                fileName,
                language,
                currentDepth,
            ).toDomainList()

    override suspend fun getPrayerNavigationTree(targetLanguage: AppLanguage): PageNodeDomain =
        prayerSource.loadPrayerNavigationTree(targetLanguage).toDomain()
}
