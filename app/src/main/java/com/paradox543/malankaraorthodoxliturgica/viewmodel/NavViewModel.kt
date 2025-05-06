package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import com.paradox543.malankaraorthodoxliturgica.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.navigation.NavigationTree
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
//        _nextSiblingIndex.value = if (currentIndex + 1 < siblings.size) currentIndex + 1 else null
        if (currentIndex > 0) {
            if (siblings[currentIndex - 1].filename.isNotEmpty()) {
                _prevSiblingIndex.value = currentIndex - 1
            } else {
                _prevSiblingIndex.value = null
            }
        } else {
            _prevSiblingIndex.value = null
        }
//        _prevSiblingIndex.value = if (currentIndex > 0) currentIndex - 1 else null

    }

    fun goToNextSibling(): String {
        val currentIndex = _currentSiblingIndex.value ?: return ""
        val siblings = _siblingNodes.value

        if (currentIndex != -1 && currentIndex < siblings.lastIndex) {
            setCurrentSiblingIndex(currentIndex + 1)
            return siblings[currentIndex + 1].filename
        }
        return ""
    }

    fun goToPrevSibling(): String {
        val currentIndex = _currentSiblingIndex.value ?: return ""
        val siblings = _siblingNodes.value

        if (currentIndex > 0) {
            setCurrentSiblingIndex(currentIndex - 1)
            return siblings[currentIndex - 1].filename
        }
        return ""
    }

    fun hasNextSibling(): Boolean {
        val currentIndex = _currentSiblingIndex.value ?: return false
        val siblings = _siblingNodes.value

        if (currentIndex + 1 < siblings.size) {
            val nextSibling = siblings[currentIndex + 1]
            return nextSibling.filename.isNotEmpty() // Check if filename is not empty
        }

        return false // Return false if there is no next sibling
    }

}