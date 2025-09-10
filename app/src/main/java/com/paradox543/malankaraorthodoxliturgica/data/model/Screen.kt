package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String, val deepLink: String? = null) {
    object Home : Screen("home", "app://liturgica/home")
    object Onboarding : Screen("onboarding")
    object PrayNow : Screen("prayNow")
    object Bible : Screen("bible", "app://liturgica/bible")
    object BibleReader : Screen("bibleReader")
    object Calendar : Screen("calendar", "app://liturgica/calendar")
    object Settings : Screen("settings", "app://liturgica/settings")
    object About : Screen("about", "app://liturgica/about")

    object Section : Screen("section/{route}") {
        const val argRoute = "route"
        const val deepLinkPattern = "app://liturgica/section/{$argRoute}"

        fun createRoute(sectionRoute: String) = "section/$sectionRoute"
        fun createDeepLink(sectionRoute: String) = "app://liturgica/section/$sectionRoute"
    }

    object Prayer : Screen("prayer/{route}") {
        const val argRoute = "route"
        const val deepLinkPattern = "app://liturgica/prayer/{$argRoute}"

        fun createRoute(prayerRoute: String) = "prayer/$prayerRoute"
        fun createDeepLink(prayerRoute: String) = "app://liturgica/prayer/$prayerRoute"
    }

    object BibleBook : Screen("bible/{bookName}") {
        const val argBook = "bookName"
        const val deepLinkPattern = "app://liturgica/bible/{$argBook}"

        fun createRoute(bookName: String) = "bible/$bookName"
        fun createDeepLink(bookName: String) = "app://liturgica/bible/$bookName"
    }

    object BibleChapter : Screen("bible/{bookIndex}/{chapterIndex}") {
        const val argBookIndex = "bookIndex"
        const val argChapterIndex = "chapterIndex"
        const val deepLinkPattern = "app://liturgica/bible/{$argBookIndex}/{$argChapterIndex}"

        fun createRoute(bookIndex: Int, chapterIndex: Int) = "bible/$bookIndex/$chapterIndex"
        fun createDeepLink(bookIndex: Int, chapterIndex: Int) = "app://liturgica/bible/$bookIndex/$chapterIndex"
    }
}
