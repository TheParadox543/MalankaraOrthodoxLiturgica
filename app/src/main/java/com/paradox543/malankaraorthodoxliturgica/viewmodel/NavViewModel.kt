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

    private val _currentNode = MutableStateFlow(rootNode)
    val currentNode: StateFlow<PageNode> = _currentNode

    private val _currentSiblingIndex = MutableStateFlow<Int?>(null)
    val currentSiblingIndex: StateFlow<Int?> = _currentSiblingIndex

    private val _nextSiblingIndex = MutableStateFlow<Int?>(null)
    val nextSiblingIndex: StateFlow<Int?> = _nextSiblingIndex

    private val _prevSiblingIndex = MutableStateFlow<Int?>(null)
    val prevSiblingIndex: StateFlow<Int?> = _prevSiblingIndex

    fun setCurrentSiblingIndex(index: Int?) {
        _currentSiblingIndex.value = index
        updateSiblingNavigationState()
    }

    private val _siblingNodes =
        MutableStateFlow<List<PageNode>>(emptyList()) // Initially empty list
    val siblingNodes: StateFlow<List<PageNode>> = _siblingNodes

    fun setSiblingNodes(nodes: List<PageNode>) {
        _siblingNodes.value = nodes
    }

    fun findNode(node: PageNode, route: String): PageNode? {
        if (node.route == route) return node
        node.children.forEach { child ->
            val result = findNode(child, route)
            if (result != null) return result
        }
        return null
    }

    private fun updateSiblingNavigationState() {
        val currentIndex = _currentSiblingIndex.value ?: return
        val siblings = _siblingNodes.value

        if (currentIndex + 1 < siblings.size) {
            if (siblings[currentIndex + 1].filename.isNotEmpty()) {
                _nextSiblingIndex.value = currentIndex + 1
            } else {
                _nextSiblingIndex.value = null
            }
        } else {
            _nextSiblingIndex.value = null
        }

        if (currentIndex > 0) {
            if (siblings[currentIndex - 1].filename.isNotEmpty()) {
                _prevSiblingIndex.value = currentIndex - 1
            } else {
                _prevSiblingIndex.value = null
            }
        } else {
            _prevSiblingIndex.value = null
        }
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

    fun getParentRoute(route: String): String? {
        val parts = route.split("_")
        return if (parts.size > 1) {
            parts.dropLast(1).joinToString("_")
        } else {
            null
        }
    }

    fun getIndexOfSibling(currentRoute: String, parentRoute: String?): Int? {
        if (parentRoute == null) {
            return null // Root has no siblings in this context
        }

        val parentNode = findNode(rootNode, parentRoute)
        return parentNode?.children?.indexOfFirst { it.route == currentRoute }
    }
}