package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.paradox543.malankaraorthodoxliturgica.model.NavigationRepository
import com.paradox543.malankaraorthodoxliturgica.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.navigation.NavigationTree
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private  val navRepository: NavigationRepository
): ViewModel() {

    val rootNode = NavigationTree.getNavigationTree()

    private val _currentNode = MutableStateFlow(rootNode)
    val currentNode: StateFlow<PageNode> = _currentNode

    private val nodeStack = mutableListOf<PageNode>()

    private val _currentSiblingIndex = MutableStateFlow<Int?>(null)
    val currentSiblingIndex: StateFlow<Int?> = _currentSiblingIndex

    fun setCurrentSiblingIndex(index: Int?) {
        _currentSiblingIndex.value = index
        updateSiblingNavigationState()
    }

    private val _siblingNodes = MutableStateFlow<List<PageNode>>(emptyList()) // Initially empty list
    val siblingNodes: StateFlow<List<PageNode>> = _siblingNodes

    fun setSiblingNodes(nodes: List<PageNode>) {
        _siblingNodes.value = nodes
        updateSiblingNavigationState()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun goBack() {
        if (nodeStack.isNotEmpty()) {
            _currentNode.value = nodeStack.removeLast()
        }
    }

    fun isAtRoot() = _currentNode.value == rootNode

    fun findNode(node: PageNode, route: String): PageNode? {
        if (node.route == route) return node
        node.children.forEach { child ->
            val result = findNode(child, route)
            if (result != null) return result
        }
        return null
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

    fun hasPrevSibling(): Boolean {
        val currentIndex = _currentSiblingIndex.value ?: return false
        val siblings = _siblingNodes.value

        // Check if the previous index exists and the previous sibling has a non-empty filename
        if (currentIndex - 1 >= 0) {
            val prevSibling = siblings[currentIndex - 1]
            return prevSibling.filename.isNotEmpty() // Check if filename is not empty
        }

        return false // Return false if there is no previous sibling
    }
    private val _hasPrevSibling = MutableStateFlow(false)
    val hasPrevSibling: StateFlow<Boolean> = _hasPrevSibling

    private val _hasNextSibling = MutableStateFlow(false)
    val hasNextSibling: StateFlow<Boolean> = _hasNextSibling

    private fun updateSiblingNavigationState() {
        val currentIndex = _currentSiblingIndex.value
        val siblings = _siblingNodes.value

        _hasPrevSibling.value = currentIndex != null &&
                currentIndex > 0 &&
                siblings[currentIndex - 1].filename.isNotEmpty()

        _hasNextSibling.value = currentIndex != null &&
                currentIndex < siblings.lastIndex &&
                siblings[currentIndex + 1].filename.isNotEmpty()
    }
}