package com.paradox543.malankaraorthodoxliturgica.data.mapping

import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetailsData
import com.paradox543.malankaraorthodoxliturgica.data.model.TitleStrData
import com.paradox543.malankaraorthodoxliturgica.domain.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.TitleStr

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