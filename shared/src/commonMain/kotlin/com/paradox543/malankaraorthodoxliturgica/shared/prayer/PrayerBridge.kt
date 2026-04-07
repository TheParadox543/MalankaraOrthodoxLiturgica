package com.paradox543.malankaraorthodoxliturgica.shared.prayer

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement

fun PrayerElement.toUi(): PrayerUiElement =
    when (this) {
        is PrayerElement.Title -> {
            PrayerUiElement(PrayerElementType.TITLE, content)
        }

        is PrayerElement.Heading -> {
            PrayerUiElement(PrayerElementType.HEADING, content)
        }

        is PrayerElement.Subheading -> {
            PrayerUiElement(PrayerElementType.SUBHEADING, content)
        }

        is PrayerElement.Prose -> {
            PrayerUiElement(PrayerElementType.PROSE, content)
        }

        is PrayerElement.Song -> {
            PrayerUiElement(PrayerElementType.SONG, content)
        }

        is PrayerElement.Subtext -> {
            PrayerUiElement(PrayerElementType.SUBTEXT, content)
        }

        // ⚠️ For now — ignore complex ones
        else -> {
            error("Unsupported element type for now")
        }
    }