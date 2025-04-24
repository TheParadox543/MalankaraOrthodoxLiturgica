package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import com.paradox543.malankaraorthodoxliturgica.model.NavigationRepository
import com.paradox543.malankaraorthodoxliturgica.model.PageNode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private  val navigationRepository: NavigationRepository
): ViewModel() {

    val navigationTree: PageNode = navigationRepository.navigationTree

    fun getNodeByRoute(route: String): PageNode? {
        return findNodeRecursive(navigationTree, route)
    }

    private fun findNodeRecursive(node: PageNode, route: String): PageNode? {
        if (node.route == route) return node
        node.children.forEach { child ->
            findNodeRecursive(child, route)?.let { return it }
        }
        return null
    }
}