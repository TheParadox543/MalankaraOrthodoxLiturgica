package com.paradox543.malankaraorthodoxliturgica.core.analytics

import kotlin.test.Test
import kotlin.test.assertEquals

class AnalyticsEventTest {

    @Test
    fun testScreenVisitedClassification() {
        val testCases = mapOf(
            "home" to "HomeScreen",
            "onboarding" to "OnboardingScreen",
            "prayNow" to "PrayNowScreen",
            "index" to "IndexScreen",
            "settings" to "SettingsScreen",
            "about" to "AboutScreen",
            "calendar" to "CalendarScreen",
            "qrScanner" to "QrScannerScreen",
            "bibleReader" to "BibleReadingScreen",
            "section/someRoute" to "SectionScreen",
            "prayer/someRoute/0" to "PrayerScreen",
            "song/someRoute" to "SongScreen",
            "bible" to "BibleScreen",
            "bible/1" to "BibleScreen",
            "bible/1/1" to "BibleScreen",
            "unknown" to "StaticScreen"
        )

        testCases.forEach { (route, expectedClass) ->
            val event = AnalyticsEvent.ScreenVisited(route, emptyMap())
            assertEquals(expectedClass, event.params["screen_class"], "Failed for route: $route")
        }
    }

    @Test
    fun testScreenVisitedResolvedName() {
        val event = AnalyticsEvent.ScreenVisited("section/{route}", mapOf("route" to "morning"))
        assertEquals("section/morning", event.params["screen_name"])
    }
}
