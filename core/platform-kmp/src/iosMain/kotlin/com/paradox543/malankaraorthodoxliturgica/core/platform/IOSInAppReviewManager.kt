package com.paradox543.malankaraorthodoxliturgica.core.platform

class IOSInAppReviewManager : InAppReviewManager {
    override suspend fun checkForReview() {
        // No-op for now on iOS
    }

    override suspend fun incrementAndGetPrayerScreenVisits(): Int {
        return 0
    }

    override suspend fun clearPrayerScreenVisitCount() {
    }

    override suspend fun getPrayerScreenVisitCount(): Int {
        return 0
    }
}
