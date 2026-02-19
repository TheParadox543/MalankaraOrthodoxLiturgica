package com.paradox543.malankaraorthodoxliturgica.data.model

import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleReadingsData
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