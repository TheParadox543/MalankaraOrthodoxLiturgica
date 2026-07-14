package com.paradox543.malankaraorthodoxliturgica.feature.prayer.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Subheading
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerRenderContext

@Composable
fun SelectedBibleReadingUI(
    selectedBibleReading: PrayerElement.PrayerBibleReading,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
) {
    val paragraph: String? =
        selectedBibleReading.readingContent
            ?.verses
            ?.joinToString(" ") { it.verse }
    selectedBibleReading.readingContent?.preface?.forEach {
        Prose(it.content)
        Spacer(Modifier.padding(4.dp))
    }
    if (selectedBibleReading.formattedReference != null && paragraph != null) {
        CollapsibleTextBlock(
            prayerElement =
                PrayerElement.CollapsibleBlock(
                    title = selectedBibleReading.formattedReference!!,
                    items =
                        listOf(
                            PrayerElement.Prose(paragraph),
                        ),
                ),
            context = context,
            filename = filename,
            onPrayerButtonClick = onPrayerButtonClick,
            subheading = true,
        )
    } else if (selectedBibleReading.formattedReference != null) {
        Subheading(selectedBibleReading.formattedReference!!)
    }
}