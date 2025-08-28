package com.paradox543.malankaraorthodoxliturgica.navigation

import android.util.Log
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
    const val ASCENSION = "ascension"
    const val PENTECOST = "pentecost"
    const val CONTEXTUAL = "contextual"
    const val AFTER_FOOD = "afterFood"
    const val BEFORE_FOOD = "beforeFood"
    const val FOR_SICK = "forSick"
    const val HOME_PRAYERS = "homePrayers"
    const val EMBRACING_THE_HAND_SONGS = "embracingTheHandSongs"

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

    private val BASE_TREE = PageNode(
        route = PrayerRoutes.ROOT,
        languages = listOf("ml", "mn", "en"),
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
            languages = listOf("ml", "mn", "en"),
            parent = parentRoute,
            children = listOf(
                prayer("lords", "commonprayers/lords.json", currentRoute, listOf("ml", "mn", "en")),
                prayer("mary", "commonprayers/mary.json", currentRoute, listOf("ml", "mn", "en")),
                prayer("kauma", "commonprayers/doxology.json", currentRoute, listOf("ml", "mn", "en")),
                prayer("kaumaSyriac", "commonprayers/trisagionSyriac.json", currentRoute, listOf("ml", "mn")),
                prayer("nicene", "commonprayers/nicenecreed.json", currentRoute, listOf("ml", "mn")),
                prayer("angels", "commonprayers/praiseOfAngels.json", currentRoute, listOf("ml", "mn")),
                prayer("cherubims", "commonprayers/praiseOfCherubims.json", currentRoute, listOf("ml", "mn")),
                prayer("morningPraise", "commonprayers/morningPraise.json", currentRoute, listOf("ml")),
                prayer("cyclic", "commonprayers/cyclicprayers.json", currentRoute, listOf("ml", "mn"))
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
            languages = listOf("ml", "mn")
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
                    listOf("ml"),
                ),
                prayer(
                    "${currentRoute}_matins",
                    "${PrayerRoutes.DAILY_PRAYERS}/$currentRoute/${PrayerRoutes.MATINS}.json",
                    currentRoute,
                    listOf("ml"),
                ),
                prayer(
                    "${currentRoute}_prime",
                    "${PrayerRoutes.DAILY_PRAYERS}/$currentRoute/${PrayerRoutes.PRIME}.json",
                    currentRoute,
                    listOf("ml"),
                ),
            ),
            languages = listOf("ml", "mn")
        )
    }

    private fun sleebaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.SLEEBA
        val children = prayerNodesDailyCanonicalHours(currentRoute, PrayerRoutes.DAILY_PRAYERS, listOf("ml", "mn"))
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
            languages = listOf("ml", "mn")
        )
    }

    private fun kyamthaSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.KYAMTHA
        val children = prayerNodesDailyCanonicalHours(currentRoute, PrayerRoutes.DAILY_PRAYERS, listOf("ml", "mn"))
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
            languages = listOf("ml", "mn")
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
            ),
            languages = listOf("ml", "mn", "en")
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
                    PrayerRoutes.ASCENSION,
                    "${PrayerRoutes.FEASTS}/${PrayerRoutes.ASCENSION}.json",
                    currentRoute,
                    listOf("ml", "mn")
                ),
                pentecostSection(currentRoute),
            ),
            languages = listOf("ml", "mn")
        )
    }

    private fun christmasSection(parentRoute: String): PageNode {
        val currentRoute = "christmas"
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer(
                    createCompleteRoute(currentRoute, "vespers"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/vespers.json",
                    currentRoute,
                    listOf("ml", "mn"),
                ),
                prayer(
                    createCompleteRoute(currentRoute, "compline"),
                    "${PrayerRoutes.FEASTS}/${currentRoute}/compline.json",
                    currentRoute,
                    listOf("ml", "mn"),
                ),
            ),
            languages = listOf("ml", "mn"),
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
                    listOf("ml", "mn"),
                )
            )
        }
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
            languages = listOf("ml", "mn"),
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
                    languages = listOf("ml", "mn", "en"),
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
            languages = listOf("ml", "mn", "en"),
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
            ),
            languages = listOf("ml", "mn")
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
            languages = listOf("ml", "mn")
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
                    languages = listOf("ml", "mn")
                )
            )
        }
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
            languages = listOf("ml", "mn"),
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
            languages = listOf("ml", "mn"),
        )
    }

    private fun contextualSection(parentRoute: String): PageNode {
        val currentRoute = PrayerRoutes.CONTEXTUAL
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = listOf(
                prayer(
                    PrayerRoutes.EMBRACING_THE_HAND_SONGS,
                    "${PrayerRoutes.CONTEXTUAL}/${PrayerRoutes.EMBRACING_THE_HAND_SONGS}.json",
                    currentRoute
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
        val qurbanaSongs = listOf(
            "transfigurationSongs"
        )
        val children = qurbanaSongs.map { song ->
            prayer(
                route = song,
                filename = "qurbanaSongs/${song.replace("_", "/")}.json",
                parentRoute = currentRoute,
                languages = listOf("ml", "mn")
            )
        }
        return PageNode(
            route = currentRoute,
            parent = parentRoute,
            children = children,
            languages = listOf("ml")
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

    private fun prayerNodesDailyCanonicalHours(currentRoute: String, extraRoute: String = "", languages: List<String> = listOf("ml")): MutableList<PageNode> {
        val children = mutableListOf<PageNode>()
        for (item in CanonicalHours) {
            if (item == PrayerRoutes.NONE && currentRoute == PrayerRoutes.SLEEBA) continue
            val childNode = createCompleteRoute(currentRoute, item)
            children.add(
                prayer(
                    childNode,
                    "$extraRoute/${childNode.replace("_", "/")}.json",
                    currentRoute,
                    languages,
                )
            )
        }
        return children
    }


    /**
     * Filters the base navigation tree to include only nodes that support the target language.
     *
     * @param targetLanguage The language code (e.g., "ml" for Malayalam, "en" for English) to filter by.
     * @return A new PageNode tree containing only the nodes (and their valid children)
     * that support the specified language. If the root node itself doesn't
     * support the language, an empty root node (with no children) is returned.
     */
    fun getNavigationTree(targetLanguage: String): PageNode {
        // Start filtering from the BASE_TREE's children.
        // We assume the root node itself is a universal container, or its languages list defines
        // if *any* part of the app is available for this language.
        // If the root node must also be language-dependent, you can check it here too.
        // For simplicity, we'll always return a root node, but its children will be filtered.

        // If the root node is meant to be skipped if its own language list doesn't contain the target,
        // you might return null from here and handle it upstream, or return BASE_TREE.copy(children = emptyList())
        // if no content is found for the language.
        if (!BASE_TREE.languages.contains(targetLanguage)) {
            // If the root node itself isn't available in the target language,
            // return an empty tree (root with no children).
            return BASE_TREE.copy(children = emptyList())
        }

        val filteredChildren = BASE_TREE.children.mapNotNull { childNode ->
            filterNodeByLanguageRecursive(childNode, targetLanguage)
        }
        // Return a new copy of the root node with only the filtered children
        return BASE_TREE.copy(children = filteredChildren)
    }

    /**
     * Recursively filters a PageNode and its children based on the target language.
     *
     * @param node The current PageNode to evaluate.
     * @param targetLanguage The language code to filter by.
     * @return A new PageNode instance with only the children that support the language,
     * or null if the current node itself does not support the target language.
     */
    private fun filterNodeByLanguageRecursive(node: PageNode, targetLanguage: String): PageNode? {
        // Step 1: Check if the current node supports the target language
        if (!node.languages.contains(targetLanguage)) {
            return null // If not, this node and its entire subtree are excluded
        }

        // Step 2: Recursively filter the children of the current node
        val filteredChildren = node.children.mapNotNull { childNode ->
            filterNodeByLanguageRecursive(childNode, targetLanguage)
        }

        // Step 3: If the current node supports the language,
        // return a NEW PageNode instance with its original properties
        // but with the newly filtered list of children.
        return node.copy(children = filteredChildren)
    }
}
