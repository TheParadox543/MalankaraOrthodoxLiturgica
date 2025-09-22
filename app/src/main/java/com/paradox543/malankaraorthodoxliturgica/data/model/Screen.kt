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
    object QrScanner : Screen("qrScanner")
    object Settings : Screen("settings", "app://liturgica/settings")
    object About : Screen("about", "app://liturgica/about")

    object Section : Screen("section/{route}") {
        const val ARG_ROUTE = "route"
        const val DEEP_LINK_PATTERN = "app://liturgica/section/{$ARG_ROUTE}"

        fun createRoute(sectionRoute: String) = "section/$sectionRoute"
        fun createDeepLink(sectionRoute: String) = "app://liturgica/section/$sectionRoute"
    }

    object Prayer : Screen("prayer/{route}/{scroll}") {
        const val ARG_ROUTE = "route"
        const val ARG_SCROLL = "scroll"
        const val DEEP_LINK_PATTERN = "app://liturgica/prayer/{$ARG_ROUTE}/{$ARG_SCROLL}"

        fun createRoute(prayerRoute: String, scroll: Int = 0) = "prayer/$prayerRoute/$scroll"
        fun createDeepLink(prayerRoute: String, scroll: Int = 0) = "app://liturgica/prayer/$prayerRoute/$scroll"
    }

    object BibleBook : Screen("bible/{bookName}") {
        const val ARG_BOOK = "bookName"
        const val DEEP_LINK_PATTERN = "app://liturgica/bible/{$ARG_BOOK}"

        fun createRoute(bookName: String) = "bible/$bookName"
        fun createDeepLink(bookName: String) = "app://liturgica/bible/$bookName"
    }

    object BibleChapter : Screen("bible/{bookIndex}/{chapterIndex}") {
        const val ARG_BOOK_INDEX = "bookIndex"
        const val ARG_CHAPTER_INDEX = "chapterIndex"
        const val DEEP_LINK_PATTERN = "app://liturgica/bible/{$ARG_BOOK_INDEX}/{$ARG_CHAPTER_INDEX}"

        fun createRoute(bookIndex: Int, chapterIndex: Int) = "bible/$bookIndex/$chapterIndex"
        fun createDeepLink(bookIndex: Int, chapterIndex: Int) = "app://liturgica/bible/$bookIndex/$chapterIndex"
    }
}
