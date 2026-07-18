package com.paradox543.malankaraorthodoxliturgica.domain.calendar.model

enum class SeasonName {
    ANNUNCIATION,
    EPIPHANY,
    GREAT_LENT,
    RESURRECTION,
    PENTECOST,
    TRANSFIGURATION,
    HOLY_CROSS,
    DUMMY,
    ;

    fun next(): SeasonName? = entries.getOrNull(ordinal + 1)

    fun nextCircular(): SeasonName = entries[(ordinal + 1) % entries.size]

    fun prev(): SeasonName? = entries.getOrNull(ordinal - 1)

    fun prevCircular(): SeasonName = entries[(ordinal - 1 + entries.size) % entries.size]
}
