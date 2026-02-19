package com.paradox543.malankaraorthodoxliturgica.data.bible.model

import kotlinx.serialization.Serializable

// Represents the template file for all preface types.
@Serializable
data class PrefaceTemplatesDto(
    val prophets: PrefaceContentDto,
    val generalEpistle: PrefaceContentDto,
    val paulineEpistle: PrefaceContentDto,
)