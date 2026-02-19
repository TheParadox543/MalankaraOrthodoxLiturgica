package com.paradox543.malankaraorthodoxliturgica.data.bible.model

import kotlinx.serialization.Serializable

@Serializable
data class DisplayTextDto(
    val en: String,
    val ml: String? = null,
)