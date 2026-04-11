package com.paradox543.malankaraorthodoxliturgica.feature.bible.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.feature.bible.viewmodel.BibleViewModel

@Composable
fun BibleBookScreen(
    onBibleNavigate: (Int, Int) -> Unit,
    bibleViewModel: BibleViewModel,
    bookIndex: Int,
    contentPadding: PaddingValues,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
) {
    val selectedLanguage by bibleViewModel.selectedLanguage.collectAsState()
    val bibleBook = bibleViewModel.getBookOrNull(bookIndex) ?: return
    val bookName = bibleBook.book.get(selectedLanguage)
    val chapters = bibleBook.chapters

    LaunchedEffect(bookName) { onScaffoldStateChanged(ScaffoldUiState.Standard(bookName)) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(72.dp),
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(count = chapters) { chapterIndex ->
            BibleChapterCard(onBibleNavigate, bookIndex, chapterIndex)
        }
    }
}

@Composable
fun BibleChapterCard(
    onBibleNavigate: (Int, Int) -> Unit,
    bookIndex: Int,
    chapterIndex: Int,
) {
    Card(
        modifier =
            Modifier
                .padding(12.dp)
                .fillMaxSize()
                .aspectRatio(1f)
                .clickable { onBibleNavigate(bookIndex, chapterIndex) },
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = (chapterIndex + 1).toString(),
            )
        }
    }
}