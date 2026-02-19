package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookNameDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleChapterDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleVerseDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.DisplayTextDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceContentDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceTemplatesData
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.ProseDto
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReadingsData
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReferenceData
import com.paradox543.malankaraorthodoxliturgica.data.model.ReferenceRangeData
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReadingsSelection
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.DisplayText
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.PrefaceContent
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain

fun ProseDto.toDomain(): PrayerElementDomain.Prose = PrayerElementDomain.Prose(this.content)

fun List<ProseDto>.toProseListDomain(): List<PrayerElementDomain.Prose> = map { it.toDomain() }

fun PrefaceContentDto.toDomain(): PrefaceContent =
    PrefaceContent(
        en = this.en.toProseListDomain(),
        ml = this.ml.toProseListDomain(),
    )

fun PrefaceTemplatesData.toDomain(): PrefaceTemplates =
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

fun ReferenceRangeData.toDomain(): ReferenceRange =
    ReferenceRange(
        startChapter = this.startChapter,
        endChapter = this.endChapter,
        startVerse = this.startVerse,
        endVerse = this.endVerse,
    )

fun BibleReferenceData.toDomain(): BibleReference =
    BibleReference(
        bookNumber = bookNumber,
        ranges = ranges.map { it.toDomain() },
    )

fun List<BibleReferenceData>.toBibleReferenceDomain(): List<BibleReference> =
    map {
        it.toDomain()
    }

fun BibleReadingsData.toDomain(): BibleReadingsSelection =
    BibleReadingsSelection(
        vespersGospel = this.vespersGospel?.map { it.toDomain() },
        matinsGospel = this.matinsGospel?.map { it.toDomain() },
        primeGospel = this.primeGospel?.map { it.toDomain() },
        oldTestament = this.oldTestament?.map { it.toDomain() },
        generalEpistle = this.generalEpistle?.map { it.toDomain() },
        paulEpistle = this.paulEpistle?.map { it.toDomain() },
        gospel = this.gospel?.map { it.toDomain() },
    )