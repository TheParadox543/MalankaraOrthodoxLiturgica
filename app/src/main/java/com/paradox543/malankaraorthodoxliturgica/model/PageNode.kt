package com.paradox543.malankaraorthodoxliturgica.model

import kotlinx.serialization.Serializable

@Serializable
data class PageNode(
    val route: String,
    val filename: String = "",
    val children: List<PageNode> = emptyList()
)