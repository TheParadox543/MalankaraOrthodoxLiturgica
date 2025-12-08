package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.model.PageNodeData
import com.paradox543.malankaraorthodoxliturgica.domain.model.PageNodeDomain

/**
 * Mappers between the data and domain representations of a page node.
 * These are recursive and will map children as well.
 */
fun PageNodeData.toDomain(): PageNodeDomain =
    PageNodeDomain(
        route = this.route,
        type = this.type,
        filename = this.filename,
        parent = this.parent,
        children = this.children.map { it.toDomain() },
        languages = this.languages.toList(),
    )

fun PageNodeDomain.toData(): PageNodeData =
    PageNodeData(
        route = this.route,
        type = this.type,
        filename = this.filename,
        parent = this.parent,
        children = this.children.map { it.toData() },
        languages = this.languages.toList(),
    )
