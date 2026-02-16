package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import java.time.LocalDateTime

class GetPrayerNodesForCurrentTimeUseCase(
    private val getRecommendedPrayersUseCase: GetRecommendedPrayersUseCase,
) {
    operator fun invoke(
        root: PageNodeDomain,
        now: LocalDateTime = LocalDateTime.now(),
    ): List<PageNodeDomain> {
        val keys = getRecommendedPrayersUseCase(now)
        return keys.mapNotNull { root.findByRoute(it) }
    }
}