package com.paradox543.malankaraorthodoxliturgica.core.platform

import android.app.Activity

/**
 * Abstraction over the Google Play In-App Review API.
 *
 * Feature modules depend on this interface; the concrete implementation lives in :app.
 */
interface InAppReviewManager {
    /**
     * Increments the prayer-screen visit counter and, if the threshold is reached,
     * triggers the in-app review prompt.
     *
     * @param activity The currently visible [Activity] used to anchor the review dialog.
     */
    suspend fun checkForReview(activity: Activity)

    /**
     * Increments the stored prayer-screen visit counter and returns the new count.
     * Does not trigger the review dialog.
     */
    suspend fun incrementAndGetPrayerScreenVisits(): Int

    /** Resets the prayer-screen visit counter to zero. */
    suspend fun clearPrayerScreenVisitCount()

    /** Returns the current prayer-screen visit count without modifying it. */
    suspend fun getPrayerScreenVisitCount(): Int
}
