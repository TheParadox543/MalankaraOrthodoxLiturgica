package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider

class FirebaseAnalyticsService(
    private val logger: FirebasePlatformLogger,
    private val appInfoProvider: AppInfoProvider,
) : AnalyticsService {
    override fun logEvent(event: AnalyticsEvent) {
        val params =
            if (event is AnalyticsEvent.Error) {
                val baseParams = event.params.toMutableMap() ?: mutableMapOf()
                val allData = "${event.description} @ ${event.location} (v${appInfoProvider.versionName})"
                baseParams.apply {
                    put("app_version", appInfoProvider.versionName)
                    put("all_data", allData)
                }
            } else {
                event.params
            }
        logger.logEvent(event.name, params)
    }
}
