package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun BibleChapterScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel,
    bookNumber: Int,
    chapterNumber: Int
) {
    val fontSize by prayerViewModel.selectedFontSize.collectAsState()
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()
    var bibleLanguage = selectedLanguage
    if (selectedLanguage == "mn") {
        bibleLanguage = "en"
    }
    val chapterData = prayerViewModel.loadBibleChapter(bookNumber, chapterNumber, bibleLanguage)
    Scaffold(
        topBar = {
            TopNavBar(navController, prayerViewModel, navViewModel)
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            items(chapterData.size) { index ->
                val verseNumber = (index + 1).toString()
                val verseText = chapterData[verseNumber]!!
                VerseItem(verseNumber, verseText)
            }
        }
    }
}

@Composable
fun VerseItem(verseNumber: String, verseText: String) {
    Row {
        Text(
            text = verseNumber,
            modifier = Modifier.padding(8.dp))
        Text(
            text = verseText,
            modifier = Modifier.padding(4.dp)
        )
    }
    HorizontalDivider()
}