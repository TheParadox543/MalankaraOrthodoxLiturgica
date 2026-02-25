package com.paradox543.malankaraorthodoxliturgica.services

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import javax.inject.Inject

class FirebaseAnalyticsService @Inject constructor(
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

    override fun logScreenVisited(
        routePattern: String,
        arguments: Map<String, String?>,
    ) {
        val screenName: String
        val screenClass: String

        when {
            // Case 1
            routePattern.startsWith("section/") && routePattern.contains("{route}") -> {
                val value = arguments["route"]
                screenName = value?.let { "section/$it" } ?: routePattern
                screenClass = "SectionScreen"
            }

            // Case 2
            routePattern.startsWith("prayerScreen/") && routePattern.contains("{route}") -> {
                val value = arguments["route"]
                screenName = value?.let { "prayerScreen/$it" } ?: routePattern
                screenClass = "PrayerScreen"
            }

            // Case 3
            routePattern.startsWith("bible/") &&
                routePattern.contains("{bookName}") &&
                !routePattern.contains("{chapterIndex}") -> {
                val bookName = arguments["bookName"]
                screenName = bookName?.let { "bible/$it" } ?: routePattern
                screenClass = "BibleBookScreen"
            }

            // Case 4
            routePattern.startsWith("bible/") &&
                routePattern.contains("{bookIndex}") &&
                routePattern.contains("{chapterIndex}") -> {
                val bookIndex = arguments["bookIndex"]
                val chapterIndex = arguments["chapterIndex"]

                screenName =
                    when {
                        bookIndex != null && chapterIndex != null -> "bible/$bookIndex/$chapterIndex"
                        bookIndex != null -> "bible/$bookIndex"
                        else -> routePattern
                    }

                screenClass = "BibleChapterScreen"
            }

            // Default
            else -> {
                screenName = routePattern
                screenClass = "StaticScreen"
            }
        }

        val bundle =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
            }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}
