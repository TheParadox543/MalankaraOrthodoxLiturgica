package com.paradox543.malankaraorthodoxliturgica.ui.screens

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
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.VerseItem
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SettingsViewModel

@Composable
fun BibleChapterScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    bibleViewModel: BibleViewModel,
    bookIndex: Int,
    chapterIndex: Int,
) {
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()
    val bibleBooks = bibleViewModel.bibleBooks
    val bibleBook = bibleBooks[bookIndex]

    val bookName: String =
        when (selectedLanguage) {
            AppLanguage.MALAYALAM -> bibleBook.book.ml
            else -> bibleBook.book.en
        }
    val title =
        if (bookIndex == 18 && selectedLanguage == AppLanguage.MALAYALAM) {
            "${chapterIndex + 1}-ാം സങ്കീർത്തനം"
        } else {
            "$bookName ${chapterIndex + 1}"
        }
    val chapterData = bibleViewModel.loadBibleChapter(bookIndex, chapterIndex, selectedLanguage)
    val (prevRoute, nextRoute) = bibleViewModel.getAdjacentChapters(bookIndex, chapterIndex)
    Scaffold(
        topBar = {
            TopNavBar(
                title,
                navController,
            )
        },
        bottomBar = {
            SectionNavBar(navController, prevRoute, nextRoute) {
                Screen.BibleChapter.createDeepLink(
                    bookIndex,
                    chapterIndex,
                )
            }
        },
    ) { innerPadding ->
        if (chapterData == null) {
            Text(
                "Error in loading Bible content.",
                Modifier.padding(innerPadding),
                MaterialTheme.colorScheme.error,
            )
        } else {
            LazyColumn(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
            ) {
                items(chapterData.verses.size) { index ->
                    val verseNumber = chapterData.verses[index].id.toString()
                    val verseText = chapterData.verses[index].verse
                    VerseItem(verseNumber, verseText)
                }
            }
        }
    }
}