package com.paradox543.malankaraorthodoxliturgica.domain.model

data class BibleReading(
    val preface: List<PrayerElementDomain>? = null,
    val verses: List<BibleVerse>,
)