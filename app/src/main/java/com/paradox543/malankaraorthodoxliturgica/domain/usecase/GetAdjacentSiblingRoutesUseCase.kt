package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.PageNodeDomain
import javax.inject.Inject

class GetAdjacentSiblingRoutesUseCase @Inject constructor(
    private val findNodeUseCase: FindNodeUseCase,
) {
    operator fun invoke(
        rootNode: PageNodeDomain,
        node: PageNodeDomain,
    ): Pair<String?, String?> {
        val parentNode = findNodeUseCase(rootNode, node.parent ?: "") ?: return Pair(null, null)

        val siblings = parentNode.children
        val index = siblings.indexOf(node)
        if (index == -1) return Pair(null, null)

        val prev = siblings.getOrNull(index - 1)
        val next = siblings.getOrNull(index + 1)

        val prevRoute = prev?.filename?.let { Screen.Prayer.createRoute(prev.route) }
        val nextRoute = next?.filename?.let { Screen.Prayer.createRoute(next.route) }

        return Pair(prevRoute, nextRoute)
    }
}
