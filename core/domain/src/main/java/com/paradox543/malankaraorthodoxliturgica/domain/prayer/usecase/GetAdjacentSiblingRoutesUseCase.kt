package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNodeDomain

class GetAdjacentSiblingRoutesUseCase {
    operator fun invoke(
        rootNode: PageNodeDomain,
        node: PageNodeDomain,
    ): Pair<String?, String?> {
        val parentNode = rootNode.findByRoute(node.parent ?: "") ?: return Pair(null, null)

        val siblings = parentNode.children
        val index = siblings.indexOf(node)
        if (index == -1) return Pair(null, null)

        val prev = siblings.getOrNull(index - 1)?.route
        val next = siblings.getOrNull(index + 1)?.route

        return Pair(prev, next)
    }
}