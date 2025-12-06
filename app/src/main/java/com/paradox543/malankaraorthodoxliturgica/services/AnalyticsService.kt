package com.paradox543.malankaraorthodoxliturgica.services

import android.os.Bundle
import androidx.savedstate.SavedState
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

    fun logScreensVisited(
        routePattern: String,
        arguments: SavedState?,
    ) {
        val screenName: String
        val screenClass: String

        when {
            // Case 1
            routePattern.startsWith("section/") && routePattern.contains("{route}") -> {
                val value = arguments?.getString("route")
                screenName = value?.let { "section/$it" } ?: routePattern
                screenClass = "SectionScreen"
            }

            // Case 2
            routePattern.startsWith("prayerScreen/") && routePattern.contains("{route}") -> {
                val value = arguments?.getString("route")
                screenName = value?.let { "prayerScreen/$it" } ?: routePattern
                screenClass = "PrayerScreen"
            }

            // Case 3
            routePattern.startsWith("bible/") &&
                routePattern.contains("{bookName}") &&
                !routePattern.contains("{chapterIndex}") -> {
                val bookName = arguments?.getString("bookName")
                screenName = bookName?.let { "bible/$it" } ?: routePattern
                screenClass = "BibleBookScreen"
            }

            // Case 4
            routePattern.startsWith("bible/") &&
                routePattern.contains("{bookIndex}") &&
                routePattern.contains("{chapterIndex}") -> {
                val bookIndex = arguments?.getString("bookIndex")
                val chapterIndex = arguments?.getString("chapterIndex")

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
