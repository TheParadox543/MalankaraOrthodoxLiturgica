package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DisplayTextData(
    val en: String,
    val ml: String? = null,
)