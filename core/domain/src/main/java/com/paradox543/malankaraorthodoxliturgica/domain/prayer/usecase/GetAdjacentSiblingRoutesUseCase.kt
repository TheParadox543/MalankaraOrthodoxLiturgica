package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode

class GetAdjacentSiblingRoutesUseCase {
    operator fun invoke(
        rootNode: PageNode,
        node: PageNode,
    ): Pair<String?, String?> {
        val parentNode = rootNode.findByRoute(node.parent ?: "") ?: return Pair(null, null)
        if (parentNode.route == "feasts" || parentNode.route == "sacraments") return Pair(null, null)

        val siblings = parentNode.children
        val index = siblings.indexOf(node)
        if (index == -1) return Pair(null, null)

        val prev = siblings.getOrNull(index - 1)?.takeIf { it.children.isEmpty() }?.route
        val next = siblings.getOrNull(index + 1)?.takeIf { it.children.isEmpty() }?.route

        return Pair(prev, next)
    }
}