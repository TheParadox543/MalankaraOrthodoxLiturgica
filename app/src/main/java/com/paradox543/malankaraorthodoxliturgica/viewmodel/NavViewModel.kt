package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.paradox543.malankaraorthodoxliturgica.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.navigation.NavigationTree
import com.paradox543.malankaraorthodoxliturgica.navigation.PrayerRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {

    val rootNode = NavigationTree.getNavigationTree()

    private val _currentSiblingIndex = MutableStateFlow<Int?>(null)
    val currentSiblingIndex: StateFlow<Int?> = _currentSiblingIndex

    private val _currentNode = MutableStateFlow<PageNode?>(null)
    val currentNode: StateFlow<PageNode?> = _currentNode

    private val _parentNode = MutableStateFlow<PageNode?>(null)
    val parentNode: StateFlow<PageNode?> = _parentNode

    fun getAdjacentSiblingRoutes(node: PageNode): Pair<String?, String?> {
        val parentRoute = node.parent
        if (_parentNode.value?.route != parentRoute) {
            _parentNode.value = findNode(rootNode, parentRoute?:"")
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
        val prevRoute = prevSiblingRoute?.let { "prayerScreen/$prevSiblingRoute" }
        val nextRoute = nextSiblingRoute?.let { "prayerScreen/$nextSiblingRoute" }

        // Return the pair of nullable String routes
        return Pair(prevRoute, nextRoute)
    }

    fun findNode(node: PageNode, route: String): PageNode? {
        if (node.route == route) return node
        node.children.forEach { child ->
            val result = findNode(child, route)
            if (result != null) return result
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        Log.d("NavViewModel", prayerList.toString())
        return prayerList.distinct()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllPrayerNodes(): List<PageNode> {
        val allNodes = mutableListOf<PageNode>()
        val list = prayNow()
        for (item in list) {
            val node = findNode(rootNode, item)
            if (node != null) {
                allNodes.add(node)
            }
        }
        return allNodes
    }
}