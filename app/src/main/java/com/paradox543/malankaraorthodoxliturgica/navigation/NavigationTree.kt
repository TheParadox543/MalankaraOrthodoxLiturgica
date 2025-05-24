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
    const val KYAMTHA = "kyamtha"
    const val SHEEMA = "sheema"
    const val QURBANA = "qurbana"
    const val FUNERAL = "funeral"
    const val MEN = "men"
    const val WOMEN = "women"
    const val WEDDING = "wedding"
    const val BAPTISM = "baptism"

//    Canonical Routes
    const val VESPERS = "vespers"
    const val COMPLINE = "compline"
    const val MATINS = "matins"
    const val PRIME = "prime"
    const val TERCE = "terce"
    const val SEXT = "sext"
    const val NONE = "none"

//    Funeral Parts
    const val FIRSTPART = "firstPart"
    const val SECONDPART = "secondPart"
    const val THIRDPART = "thirdPart"
    const val FOURTHPART = "fourthPart"

//    Qurbana Parts
    const val PREPARATION = "preparation"
    const val PARTONE = "partOne"
    const val CHAPTERONE = "chapterOne"
    const val CHAPTERTWO = "chapterTwo"
    const val CHAPTERTHREE = "chapterThree"
    const val CHAPTERFOUR = "chapterFour"
    const val CHAPTERFIVE = "chapterFive"
    const val APPENDIXONE = "appendixOne"
}

val CanonicalHours = listOf(
    PrayerRoutes.VESPERS,
    PrayerRoutes.COMPLINE,
    PrayerRoutes.MATINS,
    PrayerRoutes.PRIME,
    PrayerRoutes.TERCE,
    PrayerRoutes.SEXT,
    PrayerRoutes.NONE
)

val QurbanaParts = listOf(
    PrayerRoutes.PREPARATION,
    PrayerRoutes.PARTONE,
    PrayerRoutes.CHAPTERONE,
    PrayerRoutes.CHAPTERTWO,
    PrayerRoutes.CHAPTERTHREE,
    PrayerRoutes.CHAPTERFOUR,
    PrayerRoutes.CHAPTERFIVE
)

val FuneralParts = listOf(
    PrayerRoutes.FIRSTPART,
    PrayerRoutes.SECONDPART,
    PrayerRoutes.THIRDPART,
    PrayerRoutes.FOURTHPART
)

object NavigationTree {

    fun getNavigationTree() = PageNode(
        route = PrayerRoutes.ROOT,
        languages = listOf("ml", "mn"),
        parent = null,
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
            languages = listOf("ml", "en"),
            parent = parentRoute,
            children = listOf(
                prayer("lords", "commonprayers/lords.json", currentRoute, listOf("ml", "mn")),
                prayer("mary", "commonprayers/mary.json", currentRoute, listOf("ml", "mn")),
                prayer("kauma", "commonprayers/doxology.json", currentRoute, languages = listOf("ml", "mn")),
                prayer("nicene", "commonprayers/nicenecreed.json", currentRoute),
                prayer("angels", "commonprayers/praiseOfAngels.json", currentRoute),
                prayer("cherubims", "commonprayers/praiseOfCherubims.json", currentRoute),
                prayer("cyclic", "commonprayers/cyclicprayers.json", currentRoute)
            )
        )
    }

    private fun dailyPrayersSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.DAILY_PRAYERS
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                sleebaSection(currentRoute),
                kyamthaSection(currentRoute),
                sheemaSection(currentRoute),
//                greatLentSection(currentRoute)
            )
        )
    }

    private fun sleebaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SLEEBA
        val children = prayerNodesDailyCanonicalHours(currentRoute, PrayerRoutes.DAILY_PRAYERS)
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children
        )
    }

    private fun kyamthaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.KYAMTHA
        val children = prayerNodesDailyCanonicalHours(currentRoute, PrayerRoutes.DAILY_PRAYERS)
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children
        )
    }

    private fun sheemaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SHEEMA
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                repeatDay(currentRoute, "monday"),
                repeatDay(currentRoute, "tuesday"),
                repeatDay(currentRoute, "wednesday"),
                repeatDay(currentRoute, "thursday"),
                repeatDay(currentRoute, "friday"),
                repeatDay(currentRoute, "saturday"),
                promiyonSection(currentRoute)
            )
        )
    }

//    private fun greatLentSection(parentRoute: String): PageNode {
//        val currentRoute = PrayerRoutes.GREAT_LENT
//        return PageNode(
//            route = currentRoute,
//            children = listOf(
//                lentDay(currentRoute, "monday"),
//                lentDay(currentRoute, "tuesday"),
//                lentDay(currentRoute, "thursday")
//            )
//        )
//    }

    private fun repeatDay(parentRoute: String, day: String): PageNode {
        val currentRoute = createCompleteRoute(parentRoute, day)
        val children = prayerNodesDailyCanonicalHours(currentRoute, PrayerRoutes.DAILY_PRAYERS)
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children
        )
    }

//    private fun lentDay(parentRoute: String, day: String): PageNode {
//        val currentRoute = createCompleteRoute(parentRoute, day)
//        return PageNode(
//            route = currentRoute,
//            children = listOf(
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.VE), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.SANDHYA}.json".lowercase()),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SOOTHARA), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.SOOTHARA}.json".lowercase()),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.RATHRI), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.RATHRI}.json".lowercase()),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.PRABHATHAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.PRABHATHAM}.json".lowercase()),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.MOONAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.MOONAM}.json".lowercase()),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.AARAAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.AARAAM}.json".lowercase()),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.ONBATHAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.ONBATHAM}.json".lowercase())
//            )
//        )
//    }

    private fun promiyonSection(parentRoute: String): PageNode {
        val currentRoute = createCompleteRoute(parentRoute, "promiyon")
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer("sheemaMary", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/mary.json".lowercase(), currentRoute),
                prayer("sheemaSleeba", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/sleeba.json".lowercase(), currentRoute),
                prayer("sheemaSaints", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/saints.json".lowercase(), currentRoute),
                prayer("sheemaApostle", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/apostle.json".lowercase(), currentRoute)
            )
        )
    }

    private fun sacramentsSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SACRAMENTS
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                qurbanaSection(currentRoute),
                prayer(
                    PrayerRoutes.BAPTISM,
                    "${PrayerRoutes.SACRAMENTS}/${PrayerRoutes.BAPTISM}.json",
                    currentRoute,
                    languages = listOf("ml", "mn"),
                ),
                weddingSection(currentRoute),
                prayer(
                    "houseWarming",
                    "${PrayerRoutes.SACRAMENTS}/housewarming.json",
                    currentRoute,
                    languages = listOf("ml", "mn"),
                ),
                funeralSection(currentRoute)
            )
        )
    }

    private fun qurbanaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.QURBANA
        val children = mutableListOf<PageNode>()
        for (item in QurbanaParts) {
            val childNode = createCompleteRoute(currentRoute, item)
            children.add(
                prayer(
                    route = childNode,
                    filename = "${PrayerRoutes.SACRAMENTS}/${childNode.replace("_", "/")}.json",
                    currentRoute
                )
            )
        }
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children
        )
    }

    private fun weddingSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.WEDDING
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer(
                    createCompleteRoute(currentRoute, "ring"),
                    "${PrayerRoutes.SACRAMENTS}/$currentRoute/ring.json",
                    currentRoute,
                    languages = listOf("ml", "mn"),
                ),
                prayer(
                    createCompleteRoute(currentRoute, "crown"),
                    "${PrayerRoutes.SACRAMENTS}/$currentRoute/crown.json",
                    currentRoute,
                    languages = listOf("ml", "mn")
                )
            )
        )
    }

    private fun funeralSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.FUNERAL
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                menSection(currentRoute),
                womenSection(currentRoute)
            )
        )
    }

    private fun menSection(parentRoute: String): PageNode{
        val currentRoute = createCompleteRoute(parentRoute ,PrayerRoutes.MEN)
        val children = mutableListOf<PageNode>()
        for (item in FuneralParts) {
            val childRoute = createCompleteRoute(currentRoute, item)
            children.add(
                prayer(
                    childRoute,
                    "${PrayerRoutes.SACRAMENTS}/${childRoute.replace("_", "/")}.json",
                    currentRoute,
                    languages = listOf("ml", "mn")
                )
            )
        }
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
        )
    }

    private fun womenSection(parentRoute: String): PageNode {
        val currentRoute = createCompleteRoute(parentRoute, PrayerRoutes.WOMEN)
        val children = mutableListOf<PageNode>()
        for (item in FuneralParts) {
            val childRoute = createCompleteRoute(currentRoute, item)
            children.add(
                prayer(
                    childRoute,
                    "${PrayerRoutes.SACRAMENTS}/${childRoute.replace("_", "/")}.json",
                    currentRoute
                )
            )
        }
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
        )
    }

    private fun prayer(route: String, filename: String, parentRoute: String, languages: List<String> = listOf("ml")) = PageNode(
        route = route,
        parent = parentRoute,
        languages = languages,
        filename = filename
    )

    private fun createCompleteRoute(parentRoute: String, childRoute: String): String {
        return "${parentRoute}_$childRoute"
    }

    private fun prayerNodesDailyCanonicalHours(currentRoute: String, extraRoute: String = ""): MutableList<PageNode> {
        val children = mutableListOf<PageNode>()
        for (item in CanonicalHours) {
            if (item == PrayerRoutes.NONE && currentRoute == PrayerRoutes.SLEEBA) continue
            val childNode = createCompleteRoute(currentRoute, item)
            children.add(
                prayer(
                    childNode,
                    "$extraRoute/${childNode.replace("_", "/")}.json".lowercase(),
                    currentRoute
                )
            )
        }
        return children
    }
}
