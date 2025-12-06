package com.paradox543.malankaraorthodoxliturgica.ui.navigation

sealed class AppScreen(
    val route: String,
    val deepLink: String? = null,
) {
    object Home : AppScreen("home", "app://liturgica/home")

    object Onboarding : AppScreen("onboarding")

    object PrayNow : AppScreen("prayNow")

    object Bible : AppScreen("bible", "app://liturgica/bible")

    object BibleReader : AppScreen("bibleReader")

    object Calendar : AppScreen("calendar", "app://liturgica/calendar")

    object QrScanner : AppScreen("qrScanner")

    object Settings : AppScreen("settings", "app://liturgica/settings")

    object About : AppScreen("about", "app://liturgica/about")

    object Section : AppScreen("section/{route}") {
        const val ARG_ROUTE = "route"
        const val DEEP_LINK_PATTERN = "app://liturgica/section/{$ARG_ROUTE}"

        fun createRoute(sectionRoute: String) = "section/$sectionRoute"

        fun createDeepLink(sectionRoute: String) = "app://liturgica/section/$sectionRoute"
    }

    object Prayer : AppScreen("prayer/{route}/{scroll}") {
        const val ARG_ROUTE = "route"
        const val ARG_SCROLL = "scroll"
        const val DEEP_LINK_PATTERN = "app://liturgica/prayer/{$ARG_ROUTE}/{$ARG_SCROLL}"

        fun createRoute(
            prayerRoute: String,
            scroll: Int = 0,
        ) = "prayer/$prayerRoute/$scroll"

        fun createDeepLink(
            prayerRoute: String,
            scroll: Int = 0,
        ) = "app://liturgica/prayer/$prayerRoute/$scroll"
    }

    object Song : AppScreen("song/{route}") {
        const val ARG_ROUTE = "route"

        fun createRoute(songRoute: String) = "song/$songRoute"
    }

    object BibleBook : AppScreen("bible/{bookIndex}") {
        const val ARG_BOOK_INDEX = "bookIndex"
        const val DEEP_LINK_PATTERN = "app://liturgica/bible/{$ARG_BOOK_INDEX}"

        fun createRoute(bookIndex: Int) = "bible/$bookIndex"

        fun createDeepLink(bookIndex: Int) = "app://liturgica/bible/$bookIndex"
    }

    object BibleChapter : AppScreen("bible/{bookIndex}/{chapterIndex}") {
        const val ARG_BOOK_INDEX = "bookIndex"
        const val ARG_CHAPTER_INDEX = "chapterIndex"
        const val DEEP_LINK_PATTERN = "app://liturgica/bible/{$ARG_BOOK_INDEX}/{$ARG_CHAPTER_INDEX}"

        fun createRoute(
            bookIndex: Int,
            chapterIndex: Int,
        ) = "bible/$bookIndex/$chapterIndex"

        fun createDeepLink(
            bookIndex: Int,
            chapterIndex: Int,
        ) = "app://liturgica/bible/$bookIndex/$chapterIndex"
    }
}