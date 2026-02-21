package com.paradox543.malankaraorthodoxliturgica.domain.fakes

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

/**
 * Test fake for [PrayerRepository].
 */
class FakePrayerRepository(
    private val elementsMap: Map<String, List<PrayerElement>> = emptyMap(),
    private val navigationRoot: PageNode? = null,
    private val throwOnMissing: Boolean = false,
) : PrayerRepository {
    override suspend fun loadPrayerElements(
        fileName: String,
        language: AppLanguage,
    ): List<PrayerElement> {
        if (throwOnMissing && !elementsMap.containsKey(fileName)) {
            throw IllegalArgumentException("File not found in fake: $fileName")
        }
        return elementsMap[fileName] ?: emptyList()
    }

    override suspend fun getPrayerNavigationTree(targetLanguage: AppLanguage): PageNode =
        navigationRoot ?: PageNode(route = "root", parent = null)
}
