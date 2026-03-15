package com.paradox543.malankaraorthodoxliturgica.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.QrDialog
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.VerseItem
import com.paradox543.malankaraorthodoxliturgica.core.ui.rememberScrollAwareVisibility
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.qr.generateQrBitmap
import com.paradox543.malankaraorthodoxliturgica.ui.navigation.AppScreen
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SettingsViewModel

@Composable
fun BibleChapterScreen(
    settingsViewModel: SettingsViewModel,
    bibleViewModel: BibleViewModel,
    bookIndex: Int,
    chapterIndex: Int,
    contentPadding: PaddingValues,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
) {
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()
    val bibleBooks = bibleViewModel.bibleBooks
    val bibleBook = bibleBooks[bookIndex]
    var showQrDialog by remember { mutableStateOf(false) }

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

    // Scroll-aware visibility — same behaviour as PrayerScreen but no FAB
    val (isVisible, nestedScrollConnection) = rememberScrollAwareVisibility()
    val listState = rememberLazyListState()

    // Show bars again when the user reaches the last item
    val isScrolledToTheEnd by remember {
        derivedStateOf {
            !listState.isScrollInProgress && !listState.canScrollForward
        }
    }
    LaunchedEffect(isScrolledToTheEnd) {
        if (isScrolledToTheEnd) isVisible.value = true
    }

    // Read during composition so Compose tracks this state and recomposes when it changes
    val barsVisible = isVisible.value

    val routeProvider =
        remember(bookIndex, chapterIndex) {
            { AppScreen.BibleChapter.createDeepLink(bookIndex, chapterIndex) }
        }

    LaunchedEffect(title, prevRoute, nextRoute, barsVisible, routeProvider) {
        onScaffoldStateChanged(
            ScaffoldUiState.PrayerReading(
                title = title,
                prevRoute = prevRoute,
                nextRoute = nextRoute,
                onShowQrDialog = { showQrDialog = true },
                isVisible = barsVisible,
                nestedScrollConnection = nestedScrollConnection,
                showFab = false,
            ),
        )
    }

    // Compute synchronously during composition. Only update when padding grows (bars visible),
    // so the Spacers freeze at bars-visible height and never jump when bars hide.
    val initialTopPadding = remember { mutableStateOf(0.dp) }
    val initialBottomPadding = remember { mutableStateOf(0.dp) }
    contentPadding.calculateTopPadding().let { if (it > initialTopPadding.value) initialTopPadding.value = it }
    contentPadding.calculateBottomPadding().let { if (it > initialBottomPadding.value) initialBottomPadding.value = it }

    if (chapterData == null) {
        Text(
            "Error in loading Bible content.",
            Modifier.padding(16.dp),
            MaterialTheme.colorScheme.error,
        )
    } else {
        if (showQrDialog) {
            val qrBitmap = generateQrBitmap(routeProvider())
            QrDialog(qrBitmap) { showQrDialog = false }
        }
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .pointerInput(Unit) { detectTapGestures { isVisible.value = !isVisible.value } },
        ) {
            item {
                Spacer(Modifier.padding(top = initialTopPadding.value))
            }
            items(chapterData.verses.size) { index ->
                val verseNumber = chapterData.verses[index].id.toString()
                val verseText = chapterData.verses[index].verse
                VerseItem(verseNumber, verseText)
            }
            item {
                Spacer(Modifier.padding(bottom = initialBottomPadding.value))
            }
        }
    }
}