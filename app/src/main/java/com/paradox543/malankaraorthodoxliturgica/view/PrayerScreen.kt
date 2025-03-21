package com.paradox543.malankaraorthodoxliturgica.view

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun PrayerScreen(navController: NavController, prayerViewModel: PrayerViewModel, modifier: Modifier = Modifier){
    val prayers by prayerViewModel.prayers.collectAsState()
    val language by prayerViewModel.selectedLanguage.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    val filename by prayerViewModel.filename.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val activity = context as? Activity
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(selectedFontSize) {
        if (selectedFontSize >= 20.sp) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    LaunchedEffect(filename) {
        try {
            prayerViewModel.loadPrayers(filename, language)
            listState.scrollToItem(0)
        } catch(e: Exception) {
            navController.navigate("dummy")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = if (isLandscape) 80.dp else 8.dp), // Reduce width in landscape
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.7f else 1f) // Limit width in landscape
                .fillMaxHeight(if (isLandscape) 0.9f else 0.8f), // Limit height in portrait
            state = listState
        ) {
        items(prayers) { prayer ->
            when (prayer["type"]) {
                "heading" -> Heading(
                    text = prayer["content"] ?: "",
                    fontSize = selectedFontSize
                )

                "subheading" -> Subheading(
                    text = prayer["content"] ?: "",
                    fontSize = selectedFontSize
                )

                "prose" -> Prose(
                    text = prayer["content"] ?: "",
                    fontSize = selectedFontSize
                )

                "song" -> Song(
                    text = prayer["content"] ?: "",
                    fontSize = selectedFontSize
                )

                "subtext" -> Subtext(
                    text = prayer["content"] ?: "",
                    fontSize = selectedFontSize
                )
            }
        }
    }}
}

@Composable
fun Heading(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize*5/4,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun Subheading(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun Prose(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text.replace("\\t", "    "),
        fontSize = fontSize,
        textAlign = TextAlign.Justify,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun Song(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text.replace("\\t", "    "),
        fontSize = fontSize,
        textAlign = TextAlign.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun Subtext(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.End,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}