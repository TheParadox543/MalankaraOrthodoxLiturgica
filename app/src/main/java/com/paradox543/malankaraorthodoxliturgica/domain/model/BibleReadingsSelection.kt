package com.paradox543.malankaraorthodoxliturgica.domain.model

data class BibleReadingsSelection(
    val vespersGospel: List<BibleReference>? =  null,
    val matinsGospel: List<BibleReference>? = null,
    val primeGospel: List<BibleReference>? = null,
    val oldTestament: List<BibleReference>? = null,
    val generalEpistle: List<BibleReference>? = null,
    val paulEpistle: List<BibleReference>? = null,
    val gospel: List<BibleReference>? = null,
)