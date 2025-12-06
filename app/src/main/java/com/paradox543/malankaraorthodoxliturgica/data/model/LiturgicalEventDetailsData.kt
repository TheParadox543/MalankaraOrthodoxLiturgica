package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LiturgicalEventDetailsData(
    val type: String,
    val title: TitleStrData,
    val bibleReadings: BibleReadingsData? = null,
    val niram: Int? = null,
    val specialSongsKey: String? = null,
    val startedYear: Int? = null,
)