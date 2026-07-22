package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SeasonDto {
    @SerialName("annunciation")
    ANNUNCIATION,

    @SerialName("epiphany")
    EPIPHANY,

    @SerialName("greatLent")
    GREAT_LENT,

    @SerialName("resurrection")
    RESURRECTION,

    @SerialName("pentecost")
    PENTECOST,

    @SerialName("transfiguration")
    TRANSFIGURATION,

    @SerialName("holyCross")
    HOLY_CROSS,
}