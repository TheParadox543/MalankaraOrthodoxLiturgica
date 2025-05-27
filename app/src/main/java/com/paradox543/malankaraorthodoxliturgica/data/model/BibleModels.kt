package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BookName(
    val en: String,
    val ml: String
)

@Serializable
data class BibleDetails(
    val book: BookName,
    val chapters: Int = 1,
    val verses: Int = 1
)

@Serializable
data class Verse(
    val Verseid: String,
    val Verse: String
)

@Serializable
data class Chapter(
    val Verse: List<Verse>
)

@Serializable
data class Book(
    val Chapter: List<Chapter>
)

@Serializable
data class BibleRoot(
    val Book: List<Book>
)