package com.paradox543.malankaraorthodoxliturgica.domain.fakes

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

/**
 * Test fake for [PrayerRepository].
 */
class FakePrayerRepository(
    private val elementsMap: Map<String, List<PrayerElementDomain>> = emptyMap(),
    private val navigationRoot: PageNodeDomain? = null,
    private val throwOnMissing: Boolean = false,
) : PrayerRepository {
    override suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
    ): List<PrayerElementDomain> {
        if (throwOnMissing && !elementsMap.containsKey(fileName)) {
            throw IllegalArgumentException("File not found in fake: $fileName")
        }
        return elementsMap[fileName] ?: emptyList()
    }

    override suspend fun getPrayerNavigationTree(targetLanguage: AppLanguage): PageNodeDomain =
        navigationRoot ?: PageNodeDomain(route = "root", parent = null)
}
