package com.paradox543.malankaraorthodoxliturgica.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PrefaceContentData(
    val en: List<PrayerElementData>,
    val ml: List<PrayerElementData>,
)