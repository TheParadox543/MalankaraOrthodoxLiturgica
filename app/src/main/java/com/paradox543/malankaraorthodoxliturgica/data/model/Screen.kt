package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Onboarding : Screen("onboarding")
    data object PrayNow : Screen("prayNow")
    data object Bible : Screen("bible")
    data object BibleReader : Screen("bibleReader")
    data object Calendar : Screen("calendar")
    data object Settings : Screen("settings")
    data object About : Screen("about")


    data class Section(val sectionRoute: String) : Screen("section/$sectionRoute") {
        companion object {
            const val baseRoute = "section"
            const val argRoute = "route"
        }
    }

    data class Prayer(val prayerRoute: String) : Screen("prayer/$prayerRoute") {
        companion object {
            const val baseRoute = "prayerScreen"
            const val argRoute = "route"
        }
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
