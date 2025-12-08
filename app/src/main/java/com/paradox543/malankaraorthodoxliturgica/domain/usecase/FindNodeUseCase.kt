package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.model.PageNodeDomain
import javax.inject.Inject

class FindNodeUseCase @Inject constructor() {
    operator fun invoke(
        node: PageNodeDomain,
        route: String,
    ): PageNodeDomain? {
        if (node.route == route) return node
        for (child in node.children) {
            val result = invoke(child, route)
            if (result != null) return result
        }
        return null
    }
}
