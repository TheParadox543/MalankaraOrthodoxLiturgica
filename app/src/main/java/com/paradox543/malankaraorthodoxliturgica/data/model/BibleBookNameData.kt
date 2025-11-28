package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BibleBookNameData(
    val en: String,
    val ml: String,
)