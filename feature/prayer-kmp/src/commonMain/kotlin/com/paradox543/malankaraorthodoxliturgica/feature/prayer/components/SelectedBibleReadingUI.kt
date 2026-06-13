package com.paradox543.malankaraorthodoxliturgica.feature.prayer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement

@Composable
fun SelectedBibleReadingUI(selectedBibleReading: PrayerElement.PrayerBibleReading) {
    val paragraph: String? =
        selectedBibleReading.readingContent
            ?.verses
            ?.joinToString(" ") { it.verse }
    Column {
        selectedBibleReading.readingContent?.preface?.forEach {
            Prose(it.content)
        }
        if (paragraph != null) Prose(paragraph)
    }
}