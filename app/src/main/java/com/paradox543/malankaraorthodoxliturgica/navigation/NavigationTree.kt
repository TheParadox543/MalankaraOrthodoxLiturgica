package com.paradox543.malankaraorthodoxliturgica.navigation

import com.paradox543.malankaraorthodoxliturgica.data.model.PageNode

object PrayerRoutes {
//    Section Routes
    const val ROOT = "malankara"
    const val COMMON_PRAYERS = "commonPrayers"
    const val DAILY_PRAYERS = "dailyPrayers"
    const val SACRAMENTS = "sacraments"
    const val FEASTS = "feasts"
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
    // Feasts
    const val CHRISTMAS = "christmas"
    const val EPIPHANY = "epiphany"
    const val RECONCILIATION_SERVICE = "reconciliationService"
    const val HALF_LENT = "halfLent"
    const val ASCENSION = "ascension"
    const val PENTECOST = "pentecost"

    const val CONTEXTUAL = "contextual"
    const val AFTER_FOOD = "afterFood"
    const val BEFORE_FOOD = "beforeFood"
    const val FOR_SICK = "forSick"
    const val HOME_PRAYERS = "homePrayers"
    const val BENEDICTION_SONGS = "benedictionSongs"
    const val INTERCESSION_TO_MARY = "intercessionToMary"

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

val PentecostParts = listOf(
    PrayerRoutes.FIRSTPART,
    PrayerRoutes.SECONDPART,
    PrayerRoutes.THIRDPART,
)

object NavigationTree {

    val BASE_TREE = PageNode(
        route = PrayerRoutes.ROOT,
        parent = null,
        children = listOf(
            commonPrayersSection(PrayerRoutes.ROOT),
            dailyPrayersSection(PrayerRoutes.ROOT),
            sacramentsSection(PrayerRoutes.ROOT),
            feastsSection(PrayerRoutes.ROOT),
            contextualSection(PrayerRoutes.ROOT),
        )
    )

    private fun commonPrayersSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.COMMON_PRAYERS
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer("lords", "${PrayerRoutes.COMMON_PRAYERS}/lords.json", currentRoute),
                prayer("mary", "${PrayerRoutes.COMMON_PRAYERS}/mary.json", currentRoute),
                prayer("kauma", "${PrayerRoutes.COMMON_PRAYERS}/doxology.json", currentRoute),
                prayer("kaumaSyriac", "${PrayerRoutes.COMMON_PRAYERS}/trisagionSyriac.json", currentRoute),
                prayer("nicene", "${PrayerRoutes.COMMON_PRAYERS}/niceneCreed.json", currentRoute),
                prayer("angels", "${PrayerRoutes.COMMON_PRAYERS}/praiseOfAngels.json", currentRoute),
                prayer("cherubims", "${PrayerRoutes.COMMON_PRAYERS}/praiseOfCherubims.json", currentRoute),
                prayer("morningPraise", "${PrayerRoutes.COMMON_PRAYERS}/morningPraise.json", currentRoute),
                prayer("cyclic", "${PrayerRoutes.COMMON_PRAYERS}/cyclicPrayers.json", currentRoute)
            )
        )
    }

    private fun dailyPrayersSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.DAILY_PRAYERS
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                homePrayersSection(currentRoute),
                sleebaSection(currentRoute),
                kyamthaSection(currentRoute),
                sheemaSection(currentRoute),
//                greatLentSection(currentRoute)
            ),
        )
    }

    private fun homePrayersSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.HOME_PRAYERS
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer(
                    "${currentRoute}_vespers",
                    "${PrayerRoutes.DAILY_PRAYERS}/$currentRoute/${PrayerRoutes.VESPERS}.json",
                    currentRoute,
                ),
                prayer(
                    "${currentRoute}_matins",
                    "${PrayerRoutes.DAILY_PRAYERS}/$currentRoute/${PrayerRoutes.MATINS}.json",
                    currentRoute,
                ),
                prayer(
                    "${currentRoute}_prime",
                    "${PrayerRoutes.DAILY_PRAYERS}/$currentRoute/${PrayerRoutes.PRIME}.json",
                    currentRoute,
                ),
            ),
        )
    }

    private fun sleebaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SLEEBA
        val children = prayerNodesDailyCanonicalHours(
            currentRoute,
            PrayerRoutes.DAILY_PRAYERS
        )
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
        )
    }

    private fun kyamthaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.KYAMTHA
        val children = prayerNodesDailyCanonicalHours(
            currentRoute,
            PrayerRoutes.DAILY_PRAYERS
        )
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
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
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.VE), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.SANDHYA}.json"),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.SOOTHARA), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.SOOTHARA}.json"),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.RATHRI), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.RATHRI}.json"),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.PRABHATHAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.PRABHATHAM}.json"),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.MOONAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.MOONAM}.json"),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.AARAAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.AARAAM}.json"),
//                prayer(createCompleteRoute(currentRoute, PrayerRoutes.ONBATHAM), "${PrayerRoutes.DAILY_PRAYERS}/${PrayerRoutes.GREAT_LENT}/$day/${PrayerRoutes.ONBATHAM}.json")
//            )
//        )
//    }

    private fun promiyonSection(parentRoute: String): PageNode {
        val currentRoute = createCompleteRoute(parentRoute, "promiyon")
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer("sheemaMary", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/mary.json", currentRoute),
                prayer("sheemaSleeba", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/sleeba.json", currentRoute),
                prayer("sheemaSaints", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/saints.json", currentRoute),
                prayer("sheemaApostle", "${PrayerRoutes.DAILY_PRAYERS}/${currentRoute.replace("_", "/")}/apostle.json", currentRoute)
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
                ),
                weddingSection(currentRoute),
                prayer(
                    "houseWarming",
                    "${PrayerRoutes.SACRAMENTS}/housewarming.json",
                    currentRoute,
                ),
                funeralSection(currentRoute)
            ),
        )
    }

    private fun feastsSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.FEASTS
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                christmasSection(currentRoute),
                prayer(
                    PrayerRoutes.EPIPHANY,
                    "${PrayerRoutes.FEASTS}/${PrayerRoutes.EPIPHANY}.json",
                    currentRoute,
                ),
                prayer(
                    PrayerRoutes.RECONCILIATION_SERVICE,
                    "${PrayerRoutes.FEASTS}/${PrayerRoutes.RECONCILIATION_SERVICE}.json",
                    currentRoute,
                ),
                prayer(
                    PrayerRoutes.HALF_LENT,
                    "${PrayerRoutes.FEASTS}/${PrayerRoutes.HALF_LENT}.json",
                    currentRoute,
                ),
                prayer(
                    PrayerRoutes.ASCENSION,
                    "${PrayerRoutes.FEASTS}/${PrayerRoutes.ASCENSION}.json",
                    currentRoute,
                ),
                pentecostSection(currentRoute),
            ),
        )
    }

    private fun christmasSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.CHRISTMAS
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer(
                    createCompleteRoute(currentRoute, "vespers"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/vespers.json",
                    currentRoute,
                ),
                prayer(
                    createCompleteRoute(currentRoute, "compline"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/compline.json",
                    currentRoute,
                ),
                prayer(
                    createCompleteRoute(currentRoute, "matins"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/matins.json",
                    currentRoute,
                ),
                prayer(
                    createCompleteRoute(currentRoute, "worshipOfCross"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/worshipOfCross.json",
                    currentRoute,
                ),
                prayer(
                    createCompleteRoute(currentRoute, "prime"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/prime.json",
                    currentRoute,
                ),
                prayer(
                    createCompleteRoute(currentRoute, "terce"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/terce.json",
                    currentRoute,
                ),
                prayer(
                    createCompleteRoute(currentRoute, "sext"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/sext.json",
                    currentRoute,
                ),
            ),
        )
    }
    private fun pentecostSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.PENTECOST
        val children = mutableListOf<PageNode>()
        for (item in PentecostParts) {
            val childNode = createCompleteRoute(currentRoute, item)
            children.add(
                prayer(
                    route = childNode,
                    filename = "${PrayerRoutes.FEASTS}/${childNode.replace("_", "/")}.json",
                    currentRoute,
                )
            )
        }
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
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
                    parentRoute = currentRoute,
                )
            )
        }
        children.add(
            qurbanaSongsSection(currentRoute)
        )

        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
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
                ),
                prayer(
                    createCompleteRoute(currentRoute, "crown"),
                    "${PrayerRoutes.SACRAMENTS}/$currentRoute/crown.json",
                    currentRoute,
                )
            ),
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
            ),
        )
    }

    private fun menSection(parentRoute: String): PageNode {
        val currentRoute = createCompleteRoute(parentRoute, PrayerRoutes.MEN)
        val children = mutableListOf<PageNode>()
        for (item in FuneralParts) {
            val childRoute = createCompleteRoute(currentRoute, item)
            children.add(
                prayer(
                    childRoute,
                    "${PrayerRoutes.SACRAMENTS}/${childRoute.replace("_", "/")}.json",
                    currentRoute,
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

    private fun contextualSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.CONTEXTUAL
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer(
                    PrayerRoutes.BENEDICTION_SONGS,
                    "${PrayerRoutes.CONTEXTUAL}/${PrayerRoutes.BENEDICTION_SONGS}.json",
                    currentRoute,
                ),
                prayer(
                    PrayerRoutes.INTERCESSION_TO_MARY,
                    "${PrayerRoutes.CONTEXTUAL}/${PrayerRoutes.INTERCESSION_TO_MARY}.json",
                    currentRoute,
                ),
                prayer(
                    PrayerRoutes.BEFORE_FOOD,
                    "${PrayerRoutes.CONTEXTUAL}/${PrayerRoutes.BEFORE_FOOD}.json",
                    currentRoute
                ),
                prayer(
                    PrayerRoutes.AFTER_FOOD,
                    "${PrayerRoutes.CONTEXTUAL}/${PrayerRoutes.AFTER_FOOD}.json",
                    currentRoute
                ),
                prayer(
                    PrayerRoutes.FOR_SICK,
                    "${PrayerRoutes.CONTEXTUAL}/${PrayerRoutes.FOR_SICK}.json",
                    currentRoute
                )
            )
        )
    }

    private fun qurbanaSongsSection(parentRoute: String): PageNode {
        val currentRoute = "qurbanaSongs"
        val qurbanaSongs =
            listOf(
                "allDepartedFaithfulSongs",
                "transfigurationSongs",
                "afterHolyCrossSongs",
            )
        val children =
            qurbanaSongs.map { song ->
                prayer(
                    route = song,
                    filename = "qurbanaSongs/${song.removeSuffix("Songs")}/$song.json",
                    parentRoute = currentRoute,
                )
            }
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
        )
    }

    private fun prayer(route: String, filename: String, parentRoute: String) =
        PageNode(
            route = route,
            parent = parentRoute,
            filename = filename,
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
                    "$extraRoute/${childNode.replace("_", "/")}.json",
                    currentRoute,
                )
            )
        }
        return children
    }
}
