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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun PrayerScreen(prayerViewModel: PrayerViewModel, modifier: Modifier = Modifier){
    val prayers by prayerViewModel.prayers.collectAsState()
    val language by prayerViewModel.selectedLanguage.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    val filename by prayerViewModel.filename.collectAsState()

    LaunchedEffect(filename) {
        prayerViewModel.loadPrayers(filename, language)
    }
    LazyColumn(
        modifier.padding(8.dp)
    ) {
        items(prayers) { prayer ->
            when (prayer["type"]) {
                "heading" -> Heading(text = prayer["content"] ?: "", fontSize = selectedFontSize)
                "subheading" -> Subheading(text = prayer["content"] ?: "", fontSize = selectedFontSize)
                "prose" -> Prose(text = prayer["content"] ?: "", fontSize = selectedFontSize)
                "song" -> Song(text = prayer["content"] ?: "", fontSize = selectedFontSize)
                "subtext" -> Subtext(text = prayer["content"] ?: "", fontSize = selectedFontSize)
            }
        }
    }
}

@Composable
fun Heading(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize*5/4,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth().padding(8.dp)
    )
}

@Composable
fun Subheading(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun Prose(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.Justify,
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
}

@Composable
fun Song(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.Start,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}

@Composable
fun Subtext(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.End,
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)
    )
}