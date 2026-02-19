package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BibleReferenceData(
    val bookNumber: Int,
    val ranges: List<ReferenceRangeData>,
)