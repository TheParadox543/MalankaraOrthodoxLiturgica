package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.model.BibleBook
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun BibleScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel
) {
    val bibleChapters by prayerViewModel.bibleBooks.collectAsState()
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    var bibleLanguage = selectedLanguage
    if (selectedLanguage == "mn") {
        bibleLanguage = "en"
    }
    val oldTestamentChapters = bibleChapters.take(39)
    val newTestamentChapters = bibleChapters.drop(39)
    Scaffold(
        topBar = {
            TopNavBar(navController, prayerViewModel, navViewModel)
        },
        bottomBar = {
            BottomNavBar(navController)
        }
    ) {innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(180.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 10.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionCard("Old Testament")
            }
            items(oldTestamentChapters.size) { index ->
                BibleCard (oldTestamentChapters[index], bibleLanguage, selectedFontSize, navController)
            }
            item(span = {GridItemSpan(this.maxLineSpan)}) {
                SectionCard("New Testament")
            }
            items(newTestamentChapters.size) {index ->
                BibleCard(newTestamentChapters[index], bibleLanguage, selectedFontSize, navController)
            }
        }
    }
}

@Composable
fun SectionCard(title: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(title)
        }
    }
}

@Composable
fun BibleCard(bibleBook: BibleBook, bibleLanguage: String, selectedFontSize: TextUnit, navController: NavController){
    var bookName = ""
    when(bibleLanguage) {
        "en" -> bookName = bibleBook.book.en
        "mn" -> bookName = bibleBook.book.en
        "ml" -> bookName = bibleBook.book.ml
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .height(48.dp)
            .clickable {
                navController.navigate("bible/${bookName}")
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(bookName, fontSize = selectedFontSize)
        }
    }
}