package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReadingsData
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReferenceData
import com.paradox543.malankaraorthodoxliturgica.data.model.ReferenceRangeData
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReadingsSelection
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange

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