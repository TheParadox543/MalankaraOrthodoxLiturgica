package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService

class FirebaseAnalyticsService(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsService {
    override fun logEvent(event: AnalyticsEvent) {
        val bundle =
            Bundle().apply {
                event.params?.forEach { (key, value) ->
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Boolean -> putBoolean(key, value)
                        is Float -> putFloat(key, value)
                        is Double -> putDouble(key, value)
                    }
                }
            }

        firebaseAnalytics.logEvent(event.name, bundle)
    }
}