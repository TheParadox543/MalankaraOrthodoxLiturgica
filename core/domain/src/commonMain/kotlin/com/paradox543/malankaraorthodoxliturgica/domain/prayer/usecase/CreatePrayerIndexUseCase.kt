package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerIndexItem
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

class CreatePrayerIndexUseCase {
    suspend operator fun invoke(
        rootNode: PageNode,
        language: AppLanguage,
    ): List<PrayerIndexItem> {
        val index = mutableListOf<PrayerIndexItem>()

        rootNode.toIndex(
            index = index,
            path = emptyList(),
        )

        println("CreatePrayerIndexUseCase: Found ${index.size} prayers")
        return index
    }

    private fun PageNode.toIndex(
        index: MutableList<PrayerIndexItem>,
        path: List<String>,
    ) {
        // Current path including this node
        val currentPath = path + route

        if (filename?.endsWith(".json") == true) {
            index +=
                PrayerIndexItem(
                    prayerId = route,
                    path = currentPath,
                )
        }

        children.forEach {
            it.toIndex(
                index = index,
                path = currentPath,
            )
        }
    }
}
