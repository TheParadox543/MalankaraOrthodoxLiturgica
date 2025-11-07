package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.data.repository.NavigationRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private val navigationRepositoryImpl: NavigationRepositoryImpl,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    /**
     * The root node of the navigation tree, dynamically updated based on the selected language.
     */
    val rootNode: StateFlow<PageNode> =
        settingsRepository.selectedLanguage
            .map { language ->
                // Whenever selectedLanguage emits a new 'language',
                // this block will be re-executed, creating a new navigation tree
                navigationRepositoryImpl.getNavigationTree(language.code)
            }.stateIn(
                scope = viewModelScope, // Use viewModelScope for UI-related state
                started = SharingStarted.WhileSubscribed(5000), // Start collecting when UI observes, stop after 5s inactivity
                initialValue = navigationRepositoryImpl.getNavigationTree(AppLanguage.MALAYALAM.code), // Initial value in Malayalam
            )

    private val _currentNode = MutableStateFlow<PageNode?>(null)
    val currentNode: StateFlow<PageNode?> = _currentNode

    private val _parentNode = MutableStateFlow<PageNode?>(null)
    val parentNode: StateFlow<PageNode?> = _parentNode

    /**
     * Gets the sibling nodes for the current prayers as a pair.
     *
     * @param node The current prayer node.
     * @return A Pair containing the previous and next sibling routes, or null if they don't exist or have no filename.
     */
    fun getAdjacentSiblingRoutes(node: PageNode): Pair<String?, String?> {
        val parentRoute = node.parent
        if (_parentNode.value?.route != parentRoute) {
            _parentNode.value = findNode(rootNode.value, parentRoute ?: "")
        }

        if (_parentNode.value == null) {
            return Pair(null, null)
        }

        val siblings = _parentNode.value!!.children
        val currentIndex = siblings.indexOf(node)
        if (currentIndex == -1) {
            return Pair(null, null)
        }

        val prevSiblingNode: PageNode? = siblings.getOrNull(currentIndex - 1)
        val nextSiblingNode: PageNode? = siblings.getOrNull(currentIndex + 1)

        // Apply the condition: route is returned only if the node exists AND its filename is not null
        val prevSiblingRoute: String? = prevSiblingNode?.takeIf { it.filename != null }?.route
        val nextSiblingRoute: String? = nextSiblingNode?.takeIf { it.filename != null }?.route

        // Generate the required routes
        val prevRoute = prevSiblingRoute?.let { Screen.Prayer.createRoute(prevSiblingRoute) }
        val nextRoute = nextSiblingRoute?.let { Screen.Prayer.createRoute(nextSiblingRoute) }

        // Return the pair of nullable String routes
        return Pair(prevRoute, nextRoute)
    }

    /**
     * Finds a node in the navigation tree by its route.
     *
     * @param node The current node to search within.
     * @param route The route to search for.
     * @return The PageNode if found, or null if not found.
     */
    fun findNode(
        node: PageNode,
        route: String,
    ): PageNode? {
        if (node.route == route) return node
        node.children.forEach { child ->
            val result = findNode(child, route)
            if (result != null) return result
        }
        return null
    }

    private fun prayNow(now: LocalDateTime? = null): List<String> {
        val currentDateTime = now ?: LocalDateTime.now() // Explicitly use IST
        val prayerList = mutableListOf<String>()
        val hour = currentDateTime.hour
        var currentDay = currentDateTime.dayOfWeek.value - 1

        if (hour >= 18) {
            currentDay += 1
        }
        if (currentDay > 6) {
            currentDay = 0
        }
        val adjustedDayOfWeek = DayOfWeek.of(currentDay + 1)

        fun decideTime(option: String): List<String> {
            if (hour in 18..21) {
                prayerList.add("${option}_${PrayerRoutes.VESPERS}")
            }
            if (hour >= 18) {
                prayerList.add("${option}_${PrayerRoutes.COMPLINE}")
            }
            if (hour >= 20 || hour <= 6) {
                prayerList.add("${option}_${PrayerRoutes.MATINS}")
            }
            if (hour in 5..11) {
                prayerList.add("${option}_${PrayerRoutes.PRIME}")
            }
            if (hour in 5..17) {
                prayerList.add("${option}_${PrayerRoutes.TERCE}")
                prayerList.add("${option}_${PrayerRoutes.SEXT}")
            }
            if (hour in 11..17) {
                prayerList.add("${option}_${PrayerRoutes.NONE}")
            }
            return prayerList
        }

        if (currentDay != 6) {
            val dayName = adjustedDayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH).lowercase(Locale.ENGLISH)
            decideTime("sheema_$dayName")
        } else {
            decideTime("kyamtha")
        }

        if ((adjustedDayOfWeek == DayOfWeek.SUNDAY) && (hour in 6..13)) {
            prayerList.add("${PrayerRoutes.QURBANA}_${PrayerRoutes.PREPARATION}")
            prayerList.add("${PrayerRoutes.QURBANA}_${PrayerRoutes.PARTONE}")
            prayerList.add("${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERONE}")
            prayerList.add("${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERTWO}")
            prayerList.add("${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERTHREE}")
            prayerList.add("${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERFOUR}")
            prayerList.add("${PrayerRoutes.QURBANA}_${PrayerRoutes.CHAPTERFIVE}")
        }

        if ((adjustedDayOfWeek == DayOfWeek.SUNDAY || adjustedDayOfWeek == DayOfWeek.MONDAY) && (hour in 10..12)) {
            prayerList.add("wedding_ring")
            prayerList.add("wedding_crown")
        }

        decideTime("sleeba")
        return prayerList.distinct()
    }

    fun getAllPrayerNodes(): List<PageNode> {
        val allNodes = mutableListOf<PageNode>()
        val list = prayNow()
        for (item in list) {
            val node = findNode(rootNode.value, item)
            if (node != null) {
                allNodes.add(node)
            }
        }
        return allNodes
    }

    fun getInitialNode() {
        rootNode
    }
}