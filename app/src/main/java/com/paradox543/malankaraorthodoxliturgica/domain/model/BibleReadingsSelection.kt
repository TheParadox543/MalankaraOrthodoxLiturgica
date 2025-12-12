package com.paradox543.malankaraorthodoxliturgica.domain.model

/**
 * Holds selection of Bible readings used in liturgical contexts.
 *
 * Each property represents a named slot used by the liturgy (for example,
 * vespers gospel, matins gospel, Old Testament reading, epistles, etc.). Each
 * slot may contain multiple [BibleReference] entries.
 */
data class BibleReadingsSelection(
    val vespersGospel: List<BibleReference>? =  null,
    val matinsGospel: List<BibleReference>? = null,
    val primeGospel: List<BibleReference>? = null,
    val oldTestament: List<BibleReference>? = null,
    val generalEpistle: List<BibleReference>? = null,
    val paulEpistle: List<BibleReference>? = null,
    val gospel: List<BibleReference>? = null,
)