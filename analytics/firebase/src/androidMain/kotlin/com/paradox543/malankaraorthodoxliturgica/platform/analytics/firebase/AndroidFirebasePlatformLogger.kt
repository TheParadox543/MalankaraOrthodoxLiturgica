package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AndroidFirebasePlatformLogger(
    private val firebaseAnalytics: FirebaseAnalytics
) : FirebasePlatformLogger {
    override fun logEvent(name: String, params: Map<String, Any?>?) {
        val bundle = Bundle()
        params?.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
}
