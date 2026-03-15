package com.paradox543.malankaraorthodoxliturgica.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paradox543.malankaraorthodoxliturgica.core.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.VerseItem
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.screens.PrayerElementRenderer
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.screens.PrayerRenderContext
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.BibleViewModel

@Composable
fun BibleReadingScreen(
    onPrayerButtonClick: (String, Boolean) -> Unit,
    bibleViewModel: BibleViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
    prayerViewModel: PrayerViewModel = hiltViewModel(),
) {
    val selectedLanguage by bibleViewModel.selectedLanguage.collectAsState()
    val bibleReadings: List<BibleReference> by bibleViewModel.selectedBibleReference.collectAsState()
    val translations by bibleViewModel.translations.collectAsState()

    val context =
        remember(translations) {
            PrayerRenderContext(
                translations = translations,
                dynamicSongKey = null,           // not relevant for Bible preface
                false,     // not relevant for Bible preface
                onDynamicSongKeyChanged = {},    // not relevant for Bible preface
                onError = prayerViewModel::reportError,
            )
        }

    if (bibleReadings.isEmpty()) {
        LaunchedEffect(Unit) { onScaffoldStateChanged(ScaffoldUiState.Standard("Bible Reading", showBottomBar = false)) }
        Text(
            "No Bible readings selected.",
            Modifier.padding(16.dp),
            MaterialTheme.colorScheme.error,
        )
        return
    }
    val title =
        if (bibleReadings.size == 1) {
            bibleViewModel.formatBibleReadingEntry(bibleReadings.first(), selectedLanguage)
        } else {
            bibleViewModel.formatGospelEntry(bibleReadings, selectedLanguage)
        }

    LaunchedEffect(title) { onScaffoldStateChanged(ScaffoldUiState.Standard(title, showBottomBar = false)) }

    val bibleReading = bibleViewModel.loadBibleReading(bibleReadings, selectedLanguage)
    LazyColumn(
        modifier =
            Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
    ) {
        val preface = bibleReading.preface
        if (preface != null) {
            items(preface.size) { index ->
                PrayerElementRenderer(
                    prayerElement = preface[index],
                    context = context,
                    filename = title,
                    onPrayerButtonClick = onPrayerButtonClick,
                )
            }
            item("Divider") {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                )
            }
        }
        items(bibleReading.verses.size) { index ->
            val verseNumber = bibleReading.verses[index].id.toString()
            val verseText = bibleReading.verses[index].verse
            VerseItem(verseNumber, verseText)
        }
    }
}