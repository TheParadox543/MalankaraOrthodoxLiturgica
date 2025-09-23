package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PageNode(
    val route: String,
    val filename: String? = null,
    val parent: String?,
    val children: List<PageNode> = emptyList(),
    val languages: List<String> = listOf(),
)