package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TitleStrData(
    val en: String,
    val ml: String? = null,
)