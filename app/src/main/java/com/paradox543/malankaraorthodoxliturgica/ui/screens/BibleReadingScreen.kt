package com.paradox543.malankaraorthodoxliturgica.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.VerseItem
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.feature.bible.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel

@Composable
fun BibleReadingScreen(
//    bibleViewModel: BibleViewModel,
    calendarViewModel: CalendarViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    val selectedLanguage by calendarViewModel.selectedLanguage.collectAsState()
    val bibleReadings: List<BibleReference> by calendarViewModel.selectedBibleReference.collectAsState()

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
            calendarViewModel.formatBibleReadingEntry(bibleReadings.first(), selectedLanguage)
        } else {
            calendarViewModel.formatGospelEntry(bibleReadings, selectedLanguage)
        }

    LaunchedEffect(title) { onScaffoldStateChanged(ScaffoldUiState.Standard(title, showBottomBar = false)) }

    val bibleReading = calendarViewModel.loadBibleReading(bibleReadings, selectedLanguage)
    LazyColumn(
        modifier =
            Modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
    ) {
        val preface = bibleReading.preface
        if (preface != null) {
            items(preface) { prose ->
                Prose(prose.content, modifier = Modifier.padding(vertical = 4.dp))
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