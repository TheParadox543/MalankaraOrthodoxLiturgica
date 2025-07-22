package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.app.Activity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.google.android.play.core.review.ReviewManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppReviewManager @Inject constructor(
    private val reviewManager: ReviewManager,
    private val dataStore: DataStore<Preferences>,
) {

    // Define a key for storing the prayer screen visit count in DataStore.
    private val prayerScreenVisitCountKey = intPreferencesKey("prayer_screen_visit_count")

    /**
     * Increments the visit count for the PrayerScreen and triggers the review flow
     * if the count is a multiple of 10.
     *
     * @param activity The current activity, required to launch the review flow.
     */
    suspend fun incrementPrayerScreenVisitAndCheckForReview(activity: Activity) {
        // Increment the stored count and get the new value.
        val currentCount = incrementAndGetPrayerScreenVisits()

        // Only trigger the review flow on the 10th, 20th, 30th visit, etc.
        if (currentCount > 0 && currentCount % 10 == 0) {
            launchReviewFlow(activity)
        }
    }

    /**
     * Atomically increments the visit count in DataStore and returns the new count.
     */
    private suspend fun incrementAndGetPrayerScreenVisits(): Int {
        // dataStore.edit returns the updated Preferences object.
        val updatedPreferences = dataStore.edit { settings ->
            val currentCount = settings[prayerScreenVisitCountKey] ?: 0
            settings[prayerScreenVisitCountKey] = currentCount + 1
        }
        // We then extract the new value from the returned preferences.
        return updatedPreferences[prayerScreenVisitCountKey] ?: 1
    }

    /**
     * Initiates the Google In-App Review flow.
     * Google's API will ultimately decide whether to show the dialog or not based on its own quotas.
     */
    private fun launchReviewFlow(activity: Activity) {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // The ReviewInfo object has been successfully created.
                val reviewInfo = task.result
                reviewManager.launchReviewFlow(activity, reviewInfo)
            } else {
                // There was some error, log it but don't bother the user.
                // The review flow should not interrupt the user's journey.
                System.err.println("In-App Review flow error: ${task.exception?.message}")
            }
        }
    }
}
