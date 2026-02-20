package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.BibleReadingsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.BibleReferenceDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.ReferenceRangeDto
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReadingsSelection
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange

fun ReferenceRangeDto.toDomain(): ReferenceRange =
    ReferenceRange(
        startChapter = this.startChapter,
        endChapter = this.endChapter,
        startVerse = this.startVerse,
        endVerse = this.endVerse,
    )

fun BibleReferenceDto.toDomain(): BibleReference =
    BibleReference(
        bookNumber = bookNumber,
        ranges = ranges.map { it.toDomain() },
    )

fun List<BibleReferenceDto>.toBibleReferenceDomain(): List<BibleReference> =
    map {
        it.toDomain()
    }

fun BibleReadingsDto.toDomain(): BibleReadingsSelection =
    BibleReadingsSelection(
        vespersGospel = this.vespersGospel?.map { it.toDomain() },
        matinsGospel = this.matinsGospel?.map { it.toDomain() },
        primeGospel = this.primeGospel?.map { it.toDomain() },
        oldTestament = this.oldTestament?.map { it.toDomain() },
        generalEpistle = this.generalEpistle?.map { it.toDomain() },
        paulEpistle = this.paulEpistle?.map { it.toDomain() },
        gospel = this.gospel?.map { it.toDomain() },
    )