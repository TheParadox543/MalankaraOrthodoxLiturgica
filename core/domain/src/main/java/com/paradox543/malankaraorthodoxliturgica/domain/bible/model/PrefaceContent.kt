package com.paradox543.malankaraorthodoxliturgica.domain.bible.model

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain

data class PrefaceContent(
    val en: List<PrayerElementDomain>,
    val ml: List<PrayerElementDomain>,
)