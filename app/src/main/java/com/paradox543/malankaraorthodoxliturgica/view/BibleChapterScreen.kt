package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.navigation.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun BibleChapterScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    bibleViewModel: BibleViewModel,
    bookIndex: Int,
    chapterIndex: Int
) {
    val selectedFontSize by settingsViewModel.selectedFontSize.collectAsState()
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()
    val bibleBooks by bibleViewModel.bibleBooks.collectAsState()
    val bibleBook = bibleBooks[bookIndex]

    val bookName: String = when(selectedLanguage) {
        AppLanguage.MALAYALAM -> bibleBook.book.ml
        else -> bibleBook.book.en
    }
    val title = if (bookIndex == 18 && selectedLanguage == AppLanguage.MALAYALAM) {
        "${chapterIndex + 1}-ാം സങ്കീർത്തനം"
    } else {
        "$bookName ${chapterIndex + 1}"
    }
    val chapterData = bibleViewModel.loadBibleChapter(bookIndex, chapterIndex, selectedLanguage)
    val (prevRoute, nextRoute) = bibleViewModel.getAdjacentChapters(bookIndex, chapterIndex)
    Scaffold(
        topBar = { TopNavBar(title, navController, onActionClick = { navController.navigate("settings")}) },
        bottomBar = { SectionNavBar(navController, prevRoute, nextRoute) }
    ) { innerPadding ->
        if (chapterData == null ) {
            Text(
                "Error in loading Bible content.",
                Modifier.padding(innerPadding),
                MaterialTheme.colorScheme.error
            )
        }
        else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                items(chapterData.Verse.size) { index ->
                    val verseNumber = (index + 1).toString()
                    val verseText = chapterData.Verse[index].Verse
                    VerseItem(verseNumber, verseText, selectedFontSize.fontSize)
                }
            }
        }
    }
}

@Composable
fun VerseItem(verseNumber: String, verseText: String, fontSize: TextUnit) {
    Row {
        Text(
            text = verseNumber,
            modifier = Modifier.padding(8.dp),
            fontSize = fontSize,
        )
        Text(
            text = verseText,
            modifier = Modifier.padding(4.dp),
            fontSize = fontSize,
        )
    }
    HorizontalDivider()
}