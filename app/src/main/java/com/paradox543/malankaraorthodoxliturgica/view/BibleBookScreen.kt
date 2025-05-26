package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun BibleBookScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    bibleViewModel: BibleViewModel,
    bookName: String
) {
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()
    var bibleLanguage = selectedLanguage
    if (selectedLanguage == "mn") {
        bibleLanguage = "en"
    }
    val (bibleBook, bookIndex) = bibleViewModel.findBibleBookWithIndex(bookName, bibleLanguage)
    if (bibleBook == null){
        navController.navigate("bible") {
            popUpTo("bible") { inclusive = true }
        }
    }
    val chapters = bibleBook?.chapters ?: 1
    Scaffold(
        topBar = { TopNavBar(bookName, navController) },
        bottomBar = { BottomNavBar(navController) }
    ) {innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(72.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = chapters) { chapterIndex ->
                BibleChapterCard(navController, bookIndex?: 0, chapterIndex)
            }
        }
    }
}

@Composable
fun BibleChapterCard(navController: NavController, bookIndex: Int, chapterIndex: Int) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxSize()
            .aspectRatio(1f)
            .clickable {
                navController.navigate("bible/$bookIndex/$chapterIndex")
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
            Text(
                text = (chapterIndex+1).toString()
            )
        }
    }
}