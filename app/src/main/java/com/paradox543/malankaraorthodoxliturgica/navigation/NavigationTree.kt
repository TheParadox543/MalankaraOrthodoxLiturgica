package com.paradox543.malankaraorthodoxliturgica.navigation

import com.paradox543.malankaraorthodoxliturgica.model.PageNode

object PrayerRoutes {
//    Section Routes
    const val ROOT = "malankara"
    const val DAILY_PRAYERS = "dailyPrayers"
    const val SACRAMENTS = "sacraments"
    const val GREAT_LENT = "greatLent"
    const val SLEEBA = "sleeba"
    const val QURBANA = "qurbana"

//    Time Routes
    const val SANDHYA = "sandhya"
    const val SOOTHARA = "soothara"
    const val RATHRI = "rathri"
    const val PRABHATHAM = "prabhatham"
    const val MOONAM = "moonam"
    const val AARAAM = "aaraam"
    const val ONBATHAM = "onbatham"
    const val WEDDING = "wedding"
}

object NavigationTree {

    fun getNavigationTree() = PageNode(
        route = PrayerRoutes.ROOT,
        children = listOf(
            dailyPrayersSection(PrayerRoutes.ROOT),
            sacramentsSection(PrayerRoutes.ROOT)
        )
    )

    private fun dailyPrayersSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.DAILY_PRAYERS
        return PageNode(
            route = currentRoute,
            children = listOf(
                sleebaSection(currentRoute),
                greatLentSection(currentRoute)
            )
        )
    }

    private fun sleebaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SLEEBA
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SANDHYA), "${PrayerRoutes.DAILY_PRAYERS}/$currentRoute/${PrayerRoutes.SANDHYA}.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SOOTHARA), "${PrayerRoutes.DAILY_PRAYERS}/$currentRoute/${PrayerRoutes.SOOTHARA}.json".lowercase())
            )
        )
    }

    private fun greatLentSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.GREAT_LENT
        return PageNode(
            route = currentRoute,
            children = listOf(
                lentDay(currentRoute, "monday"),
                lentDay(currentRoute, "tuesday"),
                lentDay(currentRoute, "thursday")
            )
        )
    }

    private fun lentDay(parentRoute: String, day: String): PageNode {
        val currentRoute = createCompleteRoute(parentRoute, day)
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SANDHYA), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.SANDHYA}.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SOOTHARA), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.SOOTHARA}.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.RATHRI), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.RATHRI}.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.PRABHATHAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.PRABHATHAM}.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.MOONAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.MOONAM}.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.AARAAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.AARAAM}.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.ONBATHAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.ONBATHAM}.json".lowercase())
            )
        )
    }

    private fun sacramentsSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SACRAMENTS
        return PageNode(
            route = currentRoute,
            children = listOf(
                qurbanaSection(currentRoute),
                weddingSection(currentRoute),
                prayer("houseWarming", "${PrayerRoutes.SACRAMENTS}/houseWarming.json")
            )
        )
    }

    private fun qurbanaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.QURBANA
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer(createCompleteRoute(currentRoute, "Preparation"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_0.json"),
                prayer(createCompleteRoute(currentRoute, "Part I"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_1.json"),
                prayer(createCompleteRoute(currentRoute, "Part II Chapter 1"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_2.json"),
                prayer(createCompleteRoute(currentRoute, "Part II Chapter 2"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_3.json"),
                prayer(createCompleteRoute(currentRoute, "Part II Chapter 3"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_4.json"),
                prayer(createCompleteRoute(currentRoute, "Part II Chapter 4"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_5.json"),
                prayer(createCompleteRoute(currentRoute, "Part II Chapter 5"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_6.json")
            )
        )
    }

    private fun weddingSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.WEDDING
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer(createCompleteRoute(currentRoute, "ring"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/ring.json"),
                prayer(createCompleteRoute(currentRoute, "crown"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/crown.json")
            )
        )
    }

    private fun prayer(route: String, filename: String) = PageNode(
        route = route,
        filename = filename
    )

    private fun createCompleteRoute(parentRoute: String, childRoute: String): String {
        return "${parentRoute}_$childRoute"
    }
}
