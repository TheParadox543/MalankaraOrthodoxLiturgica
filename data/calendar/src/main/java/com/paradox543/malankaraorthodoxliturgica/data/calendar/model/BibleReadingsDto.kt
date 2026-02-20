package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class BibleReadingsDto(
    val vespersGospel: List<BibleReferenceDto>? =  null,
    val matinsGospel: List<BibleReferenceDto>? = null,
    val primeGospel: List<BibleReferenceDto>? = null,
    val oldTestament: List<BibleReferenceDto>? = null,
    val generalEpistle: List<BibleReferenceDto>? = null,
    val paulEpistle: List<BibleReferenceDto>? = null,
    val gospel: List<BibleReferenceDto>? = null,
)