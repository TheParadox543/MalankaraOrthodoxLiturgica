package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService

class FirebaseAnalyticsService(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsService {
    override fun logPrayNowItemSelection(
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

    override fun logError(
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

    override fun logShareEvent() {
        val bundle =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "share_app")
                putString(FirebaseAnalytics.Param.ITEM_ID, "app_link")
                putString(FirebaseAnalytics.Param.METHOD, "text/plain")
            }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    override fun logLanguageSelected(language: String) {
        val bundle =
            Bundle().apply {
                putString("language", language)
            }
        firebaseAnalytics.logEvent("language_selected", bundle)
    }

    override fun logQrNavigationSuccess(destinationRoute: String) {
        val bundle =
            Bundle().apply {
                putString("destination_route", destinationRoute)
                putString("destination_type", destinationRoute.substringBefore('/'))
            }
        firebaseAnalytics.logEvent("qr_navigation_success", bundle)
    }

    override fun logScreenVisited(
        routePattern: String,
        arguments: Map<String, String?>,
    ) {
        val resolvedScreenName =
            """\{([^}]+)\}""".toRegex().replace(routePattern) { match ->
                val argName = match.groupValues[1]
                arguments[argName] ?: match.value
            }

        val screenClass =
            when {
                routePattern.startsWith("section/") -> "SectionScreen"
                routePattern.startsWith("prayer/") -> "PrayerScreen"
                routePattern.startsWith("song/") -> "SongScreen"
                routePattern == "calendar" -> "CalendarScreen"
                routePattern == "qrScanner" -> "QrScannerScreen"
                routePattern.startsWith("bible") -> "BibleScreen"
                else -> "StaticScreen"
            }

        val bundle =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, resolvedScreenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
            }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    override fun logTutorialStarted() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
    }

    override fun logTutorialCompleted() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
    }
}