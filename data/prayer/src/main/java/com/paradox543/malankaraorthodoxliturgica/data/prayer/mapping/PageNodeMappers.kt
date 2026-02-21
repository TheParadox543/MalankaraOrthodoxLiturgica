package com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping

import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PageNodeDto
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode

/**
 * Mappers between the data and domain representations of a page node.
 * These are recursive and will map children as well.
 */
fun PageNodeDto.toPageNodeDomain(): PageNode =
    PageNode(
        route = this.route,
        type = this.type,
        filename = this.filename,
        parent = this.parent,
        children = this.children.map { it.toPageNodeDomain() },
        languages = this.languages.toList(),
    )

fun PageNode.toData(): PageNodeDto =
    PageNodeDto(
        route = this.route,
        type = this.type,
        filename = this.filename,
        parent = this.parent,
        children = this.children.map { it.toData() },
        languages = this.languages.toList(),
    )