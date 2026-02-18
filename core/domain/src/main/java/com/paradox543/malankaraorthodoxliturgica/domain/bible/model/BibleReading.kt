package com.paradox543.malankaraorthodoxliturgica.domain.bible.model

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain

/**
 * Represents the loaded content for a Bible reading.
 *
 * A reading may contain an optional preface block (rendered as prayer elements)
 * and the ordered list of verses for the referenced chapter(s).
 *
 * @property preface optional introductory elements to display before verses.
 * @property verses list of [BibleVerse] representing the reading body.
 */
data class BibleReading(
    val preface: List<PrayerElementDomain>? = null,
    val verses: List<BibleVerse>,
)