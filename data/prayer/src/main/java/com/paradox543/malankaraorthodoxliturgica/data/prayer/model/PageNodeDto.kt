package com.paradox543.malankaraorthodoxliturgica.data.prayer.model

import kotlinx.serialization.Serializable

/**
 * Represents a node in the navigation tree.
 *
 * @property route The unique route identifier for this node.
 * @property filename The filename associated with this node, if any (null for folders).
 * @property parent The route of the parent node, if any (null for root).
 * @property children The list of child nodes.
 * @property languages The list of language codes available for this node.
 */
@Serializable
data class PageNodeDto(
    val route: String,
    val type: String = "section",
    val filename: String? = null,
    val parent: String?,
    val children: List<PageNodeDto> = emptyList(),
    val languages: List<String> = listOf(),
)