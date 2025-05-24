package com.paradox543.malankaraorthodoxliturgica.model

import kotlinx.serialization.Serializable

@Serializable
data class BibleBook(
    val book: String = "",
    val chapters: Int = 1,
    val verses: Int = 1
)