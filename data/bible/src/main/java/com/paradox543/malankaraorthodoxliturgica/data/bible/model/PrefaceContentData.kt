package com.paradox543.malankaraorthodoxliturgica.data.bible.model

import kotlinx.serialization.Serializable

@Serializable
data class PrefaceContentData(
    val en: List<ProseDto>,
    val ml: List<ProseDto>,
)