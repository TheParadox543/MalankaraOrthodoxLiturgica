package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider

class FirebaseAnalyticsService(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val appInfoProvider: AppInfoProvider,
) : AnalyticsService {
    override fun logEvent(event: AnalyticsEvent) {
        val bundle = Bundle()
        if (event is AnalyticsEvent.Error) {
            val errorDetail = "${event.description} @ ${event.location} (v${appInfoProvider.versionName})"
            bundle.putString("error_description", errorDetail)
        } else {
            event.params?.forEach { (key, value) ->
                when (value) {
                    is String -> bundle.putString(key, value)
                    is Int -> bundle.putInt(key, value)
                    is Boolean -> bundle.putBoolean(key, value)
                    is Float -> bundle.putFloat(key, value)
                    is Double -> bundle.putDouble(key, value)
                }
            }
        }

        firebaseAnalytics.logEvent(event.name, bundle)
    }
}