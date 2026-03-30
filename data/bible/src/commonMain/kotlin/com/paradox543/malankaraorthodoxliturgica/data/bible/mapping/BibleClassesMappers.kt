package com.paradox543.malankaraorthodoxliturgica.data.bible.mapping

import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookNameDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleChapterDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleVerseDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.DisplayTextDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceContentDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceTemplatesDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.ProseDto
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.DisplayText
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.PrefaceContent
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement

fun ProseDto.toDomain(): PrayerElement.Prose = PrayerElement.Prose(this.content)

fun List<ProseDto>.toProseListDomain(): List<PrayerElement.Prose> = map { it.toDomain() }

fun PrefaceContentDto.toDomain(): PrefaceContent =
    PrefaceContent(
        en = this.en.toProseListDomain(),
        ml = this.ml.toProseListDomain(),
    )

fun PrefaceTemplatesDto.toDomain(): PrefaceTemplates =
    PrefaceTemplates(
        prophets = this.prophets.toDomain(),
        generalEpistle = this.generalEpistle.toDomain(),
        paulineEpistle = this.paulineEpistle.toDomain(),
    )

fun DisplayTextDto.toDomain(): DisplayText =
    DisplayText(
        en = this.en,
        ml = this.ml,
    )

fun BibleBookNameDto.toDomain(): BibleBookName =
    BibleBookName(
        en = this.en,
        ml = this.ml,
    )

fun BibleVerseDto.toDomain(): BibleVerse =
    BibleVerse(
        id = this.id,
        verse = this.verse,
    )

fun BibleChapterDto.toDomain(): BibleChapter =
    BibleChapter(
        book = this.book,
        chapter = this.chapter,
        verses = this.verses.map { it.toDomain() },
    )

fun BibleBookDetailsDto.toDomain(): BibleBookDetails =
    BibleBookDetails(
        book = this.book.toDomain(),
        folder = this.folder,
        chapters = this.chapters,
        verseCount = this.verseCount,
        category = this.category,
        prefaces = this.prefaces?.toDomain(),
        displayTitle = this.displayTitle?.toDomain(),
        ordinal = this.ordinal?.toDomain(),
    )

fun List<BibleBookDetailsDto>.toBibleDetailsDomain(): List<BibleBookDetails> = map { it.toDomain() }

fun BibleVerse.toData(): BibleVerseDto =
    BibleVerseDto(
        id = id,
        verse = verse,
    )

fun List<BibleVerseDto>.toBibleVerseDomain(): List<BibleVerse> = map { it.toDomain() }

fun BibleChapter.toData(): BibleChapterDto =
    BibleChapterDto(
        book = book,
        chapter = chapter,
        verses = verses.map { it.toData() },
    )