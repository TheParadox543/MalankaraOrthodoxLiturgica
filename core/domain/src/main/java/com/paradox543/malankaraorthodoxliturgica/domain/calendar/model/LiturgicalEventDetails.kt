package com.paradox543.malankaraorthodoxliturgica.domain.calendar.model

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReadingsSelection

data class LiturgicalEventDetails(
    val type: String,
    val title: TitleStr,
    val bibleReadings: BibleReadingsSelection? = null,
    val niram: Int? = null,
    val specialSongsKey: String? = null,
    val startedYear: Int? = null,
)