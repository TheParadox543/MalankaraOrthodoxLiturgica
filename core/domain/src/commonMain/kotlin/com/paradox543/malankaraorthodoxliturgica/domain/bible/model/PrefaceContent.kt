package com.paradox543.malankaraorthodoxliturgica.domain.bible.model

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement

data class PrefaceContent(
    val en: List<PrayerElement.Prose>,
    val ml: List<PrayerElement.Prose>,
)