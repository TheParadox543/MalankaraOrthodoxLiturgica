package com.paradox543.malankaraorthodoxliturgica.data.calendar.model

import kotlinx.serialization.Serializable

@Serializable
data class BibleReferenceDto(
    val bookNumber: Int,
    val ranges: List<ReferenceRangeDto>,
)