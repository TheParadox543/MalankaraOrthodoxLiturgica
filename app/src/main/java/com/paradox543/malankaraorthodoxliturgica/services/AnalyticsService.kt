package com.paradox543.malankaraorthodoxliturgica.services

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticsService @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) {
    fun logPrayNowItemSelection(
        prayerName: String,
        prayerId: String,
    ) {
        val bundle =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, prayerId)
                putString(FirebaseAnalytics.Param.ITEM_NAME, prayerName)
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "prayNow")
            }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun logError(
        description: String,
        location: String,
    ) {
        val bundle =
            Bundle().apply {
                putString("error_description", description)
                putString("error_location", location)
            }
        firebaseAnalytics.logEvent("app_error", bundle)
    }
}
