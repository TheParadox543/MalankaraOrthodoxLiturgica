package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BibleReadingsData(
    val vespersGospel: List<BibleReferenceData>? =  null,
    val matinsGospel: List<BibleReferenceData>? = null,
    val primeGospel: List<BibleReferenceData>? = null,
    val oldTestament: List<BibleReferenceData>? = null,
    val generalEpistle: List<BibleReferenceData>? = null,
    val paulEpistle: List<BibleReferenceData>? = null,
    val gospel: List<BibleReferenceData>? = null,
)