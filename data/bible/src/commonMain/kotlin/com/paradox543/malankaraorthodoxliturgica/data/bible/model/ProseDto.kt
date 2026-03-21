package com.paradox543.malankaraorthodoxliturgica.data.bible.model

import kotlinx.serialization.Serializable

@Serializable
class ProseDto(
    val type: String,
    val content: String,
)