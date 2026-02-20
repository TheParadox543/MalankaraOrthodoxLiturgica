package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarDayDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.CalendarWeekDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalEventDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.TitleStrDto
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