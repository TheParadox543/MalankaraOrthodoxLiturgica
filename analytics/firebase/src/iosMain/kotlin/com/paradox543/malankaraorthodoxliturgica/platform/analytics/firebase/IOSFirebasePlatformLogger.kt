package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

/**
 * Interface to be implemented in Swift using the official Firebase iOS SDK.
 */
interface NativeAnalyticsLogger {
    fun logEvent(name: String, params: Map<String, Any?>?)
}

class IOSFirebasePlatformLogger(
    private val nativeLogger: NativeAnalyticsLogger
) : FirebasePlatformLogger {
    override fun logEvent(name: String, params: Map<String, Any?>?) {
        nativeLogger.logEvent(name, params)
    }
}
