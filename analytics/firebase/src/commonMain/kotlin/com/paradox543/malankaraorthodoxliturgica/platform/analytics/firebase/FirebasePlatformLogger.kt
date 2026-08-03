package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

interface FirebasePlatformLogger {
    fun logEvent(name: String, params: Map<String, Any?>?)
}
