package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BibleVerseData(
    @SerialName("id")
    val id: Int,
    @SerialName("verse")
    val verse: String,
)