package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class TitleStrDto(
    val en: String,
    val ml: String? = null,
)