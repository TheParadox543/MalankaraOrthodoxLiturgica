package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.paradox543.malankaraorthodoxliturgica.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun BibleReadingScreen(
    navController: NavController,
    bibleViewModel: BibleViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val selectedFontSize by settingsViewModel.selectedFontSize.collectAsState()
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
        ) { navController.navigate("settings") }
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
            val chapterData = bibleViewModel.loadBibleReading(bibleReadings, selectedLanguage)
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                items(chapterData.size) { index ->
                    val verseNumber = chapterData[index].Verseid
                    val verseText = chapterData[index].Verse
                    VerseItem(
                        verseNumber,
                        verseText,
                        selectedFontSize.fontSize,
                    )
                }
            }
        }
    }
}