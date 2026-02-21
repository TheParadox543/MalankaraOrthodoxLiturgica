package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import java.time.LocalDateTime

class GetPrayerNodesForCurrentTimeUseCase(
    private val getRecommendedPrayersUseCase: GetRecommendedPrayersUseCase,
) {
    operator fun invoke(
        root: PageNode,
        now: LocalDateTime = LocalDateTime.now(),
    ): List<PageNode> {
        val keys = getRecommendedPrayersUseCase(now)
        return keys.mapNotNull { root.findByRoute(it) }
    }
}