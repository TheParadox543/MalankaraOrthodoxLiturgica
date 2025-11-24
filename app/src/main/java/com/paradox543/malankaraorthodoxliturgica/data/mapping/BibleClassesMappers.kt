package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.model.BibleChapterData
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleVerseData
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.model.DisplayText

fun BibleVerseData.toDomain(): BibleVerse =
    BibleVerse(
        id = this.id,
        verse = this.verse,
    )

fun BibleChapterData.toDomain(): BibleChapter =
    BibleChapter(
        book = this.book,
        chapter = this.chapter,
        verses = this.verses.map { it.toDomain() },
    )

fun BibleDetails.toDomain(): BibleBookDetails =
    BibleBookDetails(
        book = this.book,
        folder = "",
        chapters = this.chapters,
        verseCount = listOf(0),
        category = this.category,
        prefaces = this.prefaces,
        displayTitle = this.displayTitle,
        ordinal = this.ordinal,
    )

fun BibleVerse.toData(): BibleVerseData =
    BibleVerseData(
        id = id,
        verse = verse,
    )

fun BibleChapter.toData(): BibleChapterData =
    BibleChapterData(
        book = book,
        chapter = chapter,
        verses = verses.map { it.toData() },
    )