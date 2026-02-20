package com.paradox543.malankaraorthodoxliturgica.data.bible.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BibleChapterDto(
    @SerialName("book")
    val book: String,
    @SerialName("chapter")
    val chapter: Int,
    @SerialName("verses")
    val verses: List<BibleVerseDto>,
)