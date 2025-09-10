package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String, val deepLink: String? = null) {
    data object Home : Screen("home", "app://liturgica/home")
    data object Onboarding : Screen("onboarding")
    data object PrayNow : Screen("prayNow")
    data object Bible : Screen("bible", "app://liturgica/bible")
    data object BibleReader : Screen("bibleReader")
    data object Calendar : Screen("calendar", "app://liturgica/calendar")
    data object Settings : Screen("settings", "app://liturgica/settings")
    data object About : Screen("about", "app://liturgica/about")


    data class Section(val sectionRoute: String) : Screen("section/$sectionRoute") {
        companion object {
            const val baseRoute = "section"
            const val argRoute = "route"
        }
    }

    object Prayer : Screen("prayer/{route}") {
        const val argRoute = "route"
        const val deepLinkPattern = "app://liturgica/prayer/{$argRoute}"

        fun createRoute(prayerRoute: String) = "prayer/$prayerRoute"
        fun createDeepLink(prayerRoute: String) = "app://liturgica/prayer/$prayerRoute"
    }

    data class BibleBook(val bookName: String) : Screen("bible/$bookName") {
        companion object {
            const val baseRoute = "bible"
            const val argBook = "bookName"
        }
    }


    data class BibleChapter(val bookIndex: Int, val chapterIndex: Int) :
        Screen("bible/$bookIndex/$chapterIndex") {
        companion object {
            const val baseRoute = "bible"
            const val argBookIndex = "bookIndex"
            const val argChapterIndex = "chapterIndex"
        }
    }
}
