package com.paradox543.malankaraorthodoxliturgica.navigation

import com.paradox543.malankaraorthodoxliturgica.model.PageNode

object PrayerRoutes {
//    Section Routes
    const val ROOT = "malankara"
    const val COMMON_PRAYERS = "commonPrayers"
    const val DAILY_PRAYERS = "dailyPrayers"
    const val SACRAMENTS = "sacraments"
    const val GREAT_LENT = "greatLent"
    const val SLEEBA = "sleeba"
    const val SHEEMA = "sheema"
    const val QURBANA = "qurbana"
    const val FUNERAL = "funeral"
    const val MEN = "men"
    const val WOMEN = "women"
    const val WEDDING = "wedding"
    const val BAPTISM = "baptism"

//    Canonical Routes
    const val SANDHYA = "sandhya"
    const val SOOTHARA = "soothara"
    const val RATHRI = "rathri"
    const val PRABHATHAM = "prabhatham"
    const val MOONAM = "moonam"
    const val AARAAM = "aaraam"
    const val ONBATHAM = "onbatham"

//    Parts
    const val FIRSTPART = "firstPart"
    const val SECONDPART = "secondPart"
    const val THIRDPART = "thirdPart"
    const val FOURTHPART = "fourthPart"
}

object NavigationTree {

    fun getNavigationTree() = PageNode(
        route = PrayerRoutes.ROOT,
        children = listOf(
            commonPrayersSection(PrayerRoutes.ROOT),
            dailyPrayersSection(PrayerRoutes.ROOT),
            sacramentsSection(PrayerRoutes.ROOT)
        )
    )

    private fun commonPrayersSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.COMMON_PRAYERS
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer("lords", "commonprayers/lords.json"),
                prayer("mary", "commonprayers/mary.json"),
                prayer("kauma", "commonprayers/doxology.json"),
                prayer("nicene", "commonprayers/nicenecreed.json"),
                prayer("angels", "commonprayers/praiseOfCherubims.json"),
                prayer("cherubims", "krobenmaare.json")
            )
        )
    }

    private fun dailyPrayersSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.DAILY_PRAYERS
        return PageNode(
            route = currentRoute,
            children = listOf(
                sleebaSection(currentRoute),
                sheemaSection(currentRoute),
//                greatLentSection(currentRoute)
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

    private fun sheemaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SHEEMA
        return PageNode(
            route = currentRoute,
            children = listOf(
                day(currentRoute, "monday"),
                day(currentRoute, "tuesday"),
                day(currentRoute, "wednesday"),
                day(currentRoute, "thursday"),
                day(currentRoute, "friday"),
                day(currentRoute, "saturday"),
                promiyonSection(currentRoute)
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

    private fun day(parentRoute: String, day: String): PageNode {
        val currentRoute = createCompleteRoute(parentRoute, day)
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SANDHYA), "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/${currentRoute}_0.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SOOTHARA), "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/${currentRoute}_1.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.RATHRI), "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/${currentRoute}_2.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.PRABHATHAM), "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/${currentRoute}_3.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.MOONAM), "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/${currentRoute}_4.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.AARAAM), "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/${currentRoute}_5.json".lowercase()),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.ONBATHAM), "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/${currentRoute}_6.json".lowercase())
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

    private fun promiyonSection(parentRoute: String): PageNode {
        val currentRoute = createCompleteRoute(parentRoute, "promiyon")
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer("sheemaMary", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/sheema_mary.json".lowercase()),
                prayer("sheemaSleeba", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/sheema_sleeba.json".lowercase()),
                prayer("sheemaSaints", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/sheema_saints.json".lowercase()),
                prayer("sheemaApostle", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/sheema_apostle.json".lowercase())
            )
        )
    }

    private fun sacramentsSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SACRAMENTS
        return PageNode(
            route = currentRoute,
            children = listOf(
//                qurbanaSection(currentRoute),
                prayer(PrayerRoutes.BAPTISM, "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.BAPTISM}.json"),
                weddingSection(currentRoute),
                prayer("houseWarming", "${PrayerRoutes.SACRAMENTS}/houseWarming.json"),
                funeralSection(currentRoute)
            )
        )
    }

    private fun qurbanaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.QURBANA
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer(createCompleteRoute(currentRoute, "preparation"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_0.json"),
                prayer(createCompleteRoute(currentRoute, "partOne"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_1.json"),
                prayer(createCompleteRoute(currentRoute, "chapterOne"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_2.json"),
                prayer(createCompleteRoute(currentRoute, "chapterTwo"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_3.json"),
                prayer(createCompleteRoute(currentRoute, "chapterThree"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_4.json"),
                prayer(createCompleteRoute(currentRoute, "chapterFour"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_5.json"),
                prayer(createCompleteRoute(currentRoute, "chapterFive"), "${PrayerRoutes.SACRAMENTS}/$currentRoute/qurbana_6.json")
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

    private fun funeralSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.FUNERAL
        return PageNode(
            route = currentRoute,
            children = listOf(
                menSection(),
                womenSection()
            )
        )
    }

    private fun menSection(): PageNode{
        val currentRoute = PrayerRoutes.MEN
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.FIRSTPART), filename = "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.FUNERAL}/funeralMen_0.json"),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SECONDPART), filename = "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.FUNERAL}/funeralMen_1.json"),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.THIRDPART), filename = "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.FUNERAL}/funeralMen_2.json"),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.FOURTHPART), filename = "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.FUNERAL}/funeralMen_3.json")
            )
        )
    }

    private fun womenSection(): PageNode {
        val currentRoute = PrayerRoutes.WOMEN
        return PageNode(
            route = currentRoute,
            children = listOf(
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.FIRSTPART), filename = "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.FUNERAL}/funeralWomen_0.json"),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SECONDPART), filename = "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.FUNERAL}/funeralWomen_1.json"),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.THIRDPART), filename = "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.FUNERAL}/funeralWomen_2.json"),
                prayer(createCompleteRoute(currentRoute, PrayerRoutes.FOURTHPART), filename = "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.FUNERAL}/funeralWomen_3.json")
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
