package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain
import com.paradox543.malankaraorthodoxliturgica.ui.navigation.AppScreen
import javax.inject.Inject

class GetAdjacentSiblingRoutesUseCase @Inject constructor() {
    operator fun invoke(
        rootNode: PageNodeDomain,
        node: PageNodeDomain,
    ): Pair<String?, String?> {
        val parentNode = rootNode.findByRoute(node.parent ?: "") ?: return Pair(null, null)

        val siblings = parentNode.children
        val index = siblings.indexOf(node)
        if (index == -1) return Pair(null, null)

        val prev = siblings.getOrNull(index - 1)
        val next = siblings.getOrNull(index + 1)

        val prevRoute = prev?.filename?.let { AppScreen.Prayer.createRoute(prev.route) }
        val nextRoute = next?.filename?.let { AppScreen.Prayer.createRoute(next.route) }

        return Pair(prevRoute, nextRoute)
    }
}
