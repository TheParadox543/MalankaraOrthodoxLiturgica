package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BookName(
    val en: String,
    val ml: String
)

@Serializable
data class BibleBook(
    val book: BookName,
    val chapters: Int = 1,
    val verses: Int = 1
)
