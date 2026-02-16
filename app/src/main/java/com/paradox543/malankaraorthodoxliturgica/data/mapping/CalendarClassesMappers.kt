package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDayDto
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeekDto
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetailsData
import com.paradox543.malankaraorthodoxliturgica.data.model.TitleStrData
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.TitleStr

fun TitleStrData.toDomain(): TitleStr =
    TitleStr(
        en = this.en,
        ml = this.ml,
    )

fun LiturgicalEventDetailsData.toDomain(): LiturgicalEventDetails =
    LiturgicalEventDetails(
        type = this.type,
        title = this.title.toDomain(),
        bibleReadings = this.bibleReadings?.toDomain(),
        niram = this.niram,
        specialSongsKey = this.specialSongsKey,
        startedYear = this.startedYear,
    )

fun List<LiturgicalEventDetailsData>.toLiturgicalEventsDetailsDomain(): List<LiturgicalEventDetails> = map { it.toDomain() }

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