package com.example.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun PrayerScreen(prayerViewModel: PrayerViewModel, modifier: Modifier = Modifier){
    val prayers by prayerViewModel.prayers.collectAsState()
    val language by prayerViewModel.selectedLanguage.collectAsState()
    val filename by prayerViewModel.filename.collectAsState()

    LaunchedEffect(filename) {
        prayerViewModel.loadPrayers(filename, language)
        prayerViewModel.setFilename(filename)
    }
    LazyColumn(
        modifier.padding(8.dp)
    ) {
        items(prayers) { prayer ->
            when (prayer["type"]) {
                "heading" -> Heading(text = prayer["content"] ?: "")
                "subheading" -> Subheading(text = prayer["content"] ?: "")
                "prose" -> Prose(text = prayer["content"] ?: "")
                "song" -> Song(text = prayer["content"] ?: "")
                "subtext" -> Subtext(text = prayer["content"] ?: "")
            }
        }
    }
}

@Composable
fun Heading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth().padding(8.dp)
    )
}

@Composable
fun Subheading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun Prose(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        textAlign = TextAlign.Justify,
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
}

@Composable
fun Song(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        textAlign = TextAlign.Start,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}

@Composable
fun Subtext(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        textAlign = TextAlign.End,
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
}