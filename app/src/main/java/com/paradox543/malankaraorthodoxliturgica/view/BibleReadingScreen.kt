package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.VerseItem
import com.paradox543.malankaraorthodoxliturgica.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun BibleReadingScreen(
    navController: NavController,
    bibleViewModel: BibleViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    prayerViewModel: PrayerViewModel = hiltViewModel(),
) {
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()
    val bibleReadings by bibleViewModel.selectedBibleReference.collectAsState()

    if (bibleReadings.isEmpty()) {
        Text(
            "No Bible readings selected.",
            Modifier.padding(16.dp),
            MaterialTheme.colorScheme.error
        )
        return
    }
    val title = if (bibleReadings.size == 1) {
        bibleViewModel.formatBibleReadingEntry(bibleReadings.first(), selectedLanguage)
    } else {
        bibleViewModel.formatGospelEntry(bibleReadings, selectedLanguage)
    }

    Scaffold(
        topBar = { TopNavBar(
            title,
            navController
        )
        },
    ) { innerPadding ->
        if (bibleReadings.isEmpty() ) {
            Text(
                "Error in loading Bible content.",
                Modifier.padding(innerPadding),
                MaterialTheme.colorScheme.error
            )
        }
        else {
            val bibleReading = bibleViewModel.loadBibleReading(bibleReadings, selectedLanguage)
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                if (bibleReading.preface != null) {
                    items(bibleReading.preface.size) { index ->
                        PrayerElementRenderer(
                            prayerElement = bibleReading.preface[index],
                            prayerViewModel = prayerViewModel,
                            filename = title,
                            navController = navController,
                        )
                    }
                    item("Divider") {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 4.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    }
                }
                items(bibleReading.verses.size) { index ->
                    val verseNumber = bibleReading.verses[index].Verseid
                    val verseText = bibleReading.verses[index].Verse
                    VerseItem(
                        verseNumber,
                        verseText,
                    )
                }
            }
        }
    }
}