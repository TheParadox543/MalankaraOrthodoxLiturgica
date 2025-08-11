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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleDetails
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun BibleScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    bibleViewModel: BibleViewModel,
) {
    val bibleChapters by bibleViewModel.bibleBooks.collectAsState()
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()

    val oldTestamentChapters = bibleChapters.take(39)
    val newTestamentChapters = bibleChapters.drop(39)

    val title = when(selectedLanguage){
        AppLanguage.MALAYALAM -> "വേദപുസ്തകം"
        else -> "Bible"
    }

    Scaffold(
        topBar = { TopNavBar(title, navController) },
        bottomBar = { BottomNavBar(navController) }
    ) {innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(180.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                when(selectedLanguage) {
                    AppLanguage.MALAYALAM -> SectionCard("പഴയ നിയമം")
                    else -> SectionCard("Old Testament")
                }
            }
            items(oldTestamentChapters.size) { index ->
                BibleCard (oldTestamentChapters[index], selectedLanguage, navController)
            }
            item(span = {GridItemSpan(this.maxLineSpan)}) {
                when(selectedLanguage){
                    AppLanguage.MALAYALAM -> SectionCard("പുതിയ നിയമം")
                    else -> SectionCard("New Testament")
                }
            }
            items(newTestamentChapters.size) {index ->
                BibleCard(newTestamentChapters[index], selectedLanguage, navController)
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
fun BibleCard(bibleDetails: BibleDetails, selectedLanguage: AppLanguage, navController: NavController){
    val bookName = when(selectedLanguage) {
        AppLanguage.MALAYALAM -> bibleDetails.book.ml
        else -> bibleDetails.book.en
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
            Text(bookName, style = MaterialTheme.typography.bodyMedium)
        }
    }
}