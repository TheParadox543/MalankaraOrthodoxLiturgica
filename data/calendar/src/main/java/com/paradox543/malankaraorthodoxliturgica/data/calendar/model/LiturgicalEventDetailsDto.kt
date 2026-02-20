package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class LiturgicalEventDetailsDto(
    val type: String,
    val title: TitleStrDto,
    val bibleReadings: BibleReadingsDto? = null,
    val niram: Int? = null,
    val specialSongsKey: String? = null,
    val startedYear: Int? = null,
)