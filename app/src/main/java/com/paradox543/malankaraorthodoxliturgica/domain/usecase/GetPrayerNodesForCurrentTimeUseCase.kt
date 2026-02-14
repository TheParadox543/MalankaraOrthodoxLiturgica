package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import java.time.LocalDateTime
import javax.inject.Inject

class GetPrayerNodesForCurrentTimeUseCase @Inject constructor(
    private val getRecommendedPrayersUseCase: GetRecommendedPrayersUseCase,
    private val findNodeUseCase: FindNodeUseCase,
) {
    operator fun invoke(
        root: PageNodeDomain,
        now: LocalDateTime = LocalDateTime.now(),
    ): List<PageNodeDomain> {
        val keys = getRecommendedPrayersUseCase(now)
        return keys.mapNotNull { findNodeUseCase(root, it) }
    }
}
