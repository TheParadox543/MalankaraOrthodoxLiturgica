package com.paradox543.malankaraorthodoxliturgica.data.calendar.mapping

import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.BibleReadingsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.BibleReferenceDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarDayDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarWeekDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalEventDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.ReferenceRangeDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.TitleStrDto
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReadingsSelection
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.TitleStr

fun TitleStrDto.toDomain(): TitleStr =
    TitleStr(
        en = this.en,
        ml = this.ml,
    )

fun LiturgicalEventDetailsDto.toDomain(): LiturgicalEventDetails =
    LiturgicalEventDetails(
        type = this.type,
        title = this.title.toDomain(),
        bibleReadings = this.bibleReadings?.toDomain(),
        niram = this.niram,
        specialSongsKey = this.specialSongsKey,
        startedYear = this.startedYear,
    )

fun List<LiturgicalEventDetailsDto>.toLiturgicalEventsDetailsDomain(): List<LiturgicalEventDetails> = map { it.toDomain() }

fun CalendarDayDto.toDomain(): CalendarDay =
    CalendarDay(
        date = this.date,
        events = this.events.toLiturgicalEventsDetailsDomain(),
    )

fun List<CalendarDayDto>.toCalendarDaysDomain(): List<CalendarDay> = map { it.toDomain() }

fun CalendarWeekDto.toDomain(): CalendarWeek =
    CalendarWeek(
        days = this.days.toCalendarDaysDomain(),
    )

fun List<CalendarWeekDto>.toCalendarWeeksDomain(): List<CalendarWeek> = map { it.toDomain() }

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