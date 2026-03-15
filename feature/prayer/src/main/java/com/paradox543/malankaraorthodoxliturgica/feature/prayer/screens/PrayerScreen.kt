package com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Heading
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.QrDialog
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Song
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Source
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Subheading
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Subtext
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Title
import com.paradox543.malankaraorthodoxliturgica.core.ui.rememberScrollAwareVisibility
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.components.AlternativePrayersUI
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.components.ErrorBlock
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.qr.generateQrBitmap
import kotlinx.coroutines.delay

@Composable
fun PrayerScreen(
    onPrayerButtonClick: (String, Boolean) -> Unit,
    prayerViewModel: PrayerViewModel,
    prayerNavViewModel: PrayerNavViewModel,
    node: PageNode,
    scrollIndex: Int = 0,
    contentPadding: PaddingValues = PaddingValues(),
    onQrDialogShow: (String, Int) -> String,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    val prayers by prayerViewModel.prayers.collectAsState()
    val translations by prayerViewModel.translations.collectAsState()
    val songScrollState by prayerViewModel.songScrollState.collectAsState()
    val dynamicSongKey by prayerViewModel.dynamicSongKey.collectAsState()

    var title = ""
    for (item in node.route.split("_")) {
        title += (translations[item] ?: item) + " "
    }
    var showQrDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val (isVisible, nestedScrollConnection) = rememberScrollAwareVisibility()

    val currentFilename = node.filename ?: "NoFileNameFound"
    val (prevNodeRoute, nextNodeRoute) = prayerNavViewModel.getAdjacentRoutes(node)

    // Ensure prayers are loaded only when filename changes
    LaunchedEffect(currentFilename) {
        prayerViewModel.loadPrayerElements(currentFilename)
    }

    // Increment count of prayer screen visits for in-app review
    LaunchedEffect(Unit) {
        prayerViewModel.onPrayerScreenOpened()
    }

    // Store the initial system bar padding values
    val initialTopPadding = remember { mutableStateOf(0.dp) }
    val initialBottomPadding = remember { mutableStateOf(0.dp) }

    val listState =
        rememberSaveable(
            saver = LazyListState.Saver,
            key = currentFilename,
        ) {
            LazyListState()
        }

    val renderContext =
        remember(translations, dynamicSongKey, songScrollState) {
            PrayerRenderContext(
                translations = translations,
                dynamicSongKey = dynamicSongKey,
                isSongHorizontalScroll = songScrollState,
                onDynamicSongKeyChanged = prayerViewModel::setDynamicSongKey,
                onError = prayerViewModel::reportError,
            )
        }

    // Observe if the LazyColumn has been scrolled to its very end
    val isScrolledToTheEnd by remember {
        derivedStateOf {
            !listState.isScrollInProgress && !listState.canScrollForward // True if there's no more content to scroll down to
        }
    }

    // React to the scroll state change
    LaunchedEffect(isScrolledToTheEnd) {
        if (isScrolledToTheEnd) {
            isVisible.value = true // Make bars visible when scrolled to the end
        }
    }

    // Read during composition so Compose tracks this state and recomposes when it changes
    val barsVisible = isVisible.value

    LaunchedEffect(title, prevNodeRoute, nextNodeRoute, barsVisible) {
        onScaffoldStateChanged(
            ScaffoldUiState.PrayerReading(
                title = title,
                prevRoute = prevNodeRoute,
                nextRoute = nextNodeRoute,
                onShowQrDialog = {
                    showQrDialog = true
                },
                isVisible = barsVisible,
                nestedScrollConnection = nestedScrollConnection,
            ),
        )
    }

    // Compute synchronously during composition. Only update when padding grows (bars visible),
    // so the Spacers freeze at bars-visible height and never jump when bars hide.
    contentPadding.calculateTopPadding().let { if (it > initialTopPadding.value) initialTopPadding.value = it }
    contentPadding.calculateBottomPadding().let { if (it > initialBottomPadding.value) initialBottomPadding.value = it }

    LaunchedEffect(Unit) {
        var retryCount = 0
        if (scrollIndex > 0) {
            while (listState.firstVisibleItemIndex != scrollIndex && retryCount < 10) {
                Log.d("QR in Prayer AppScreen", "Detected scroll from Qr: $scrollIndex")
                listState.scrollToItem(scrollIndex)
                Log.d("QR in Prayer AppScreen", "Scrolled to item: ${listState.firstVisibleItemIndex}")
                retryCount++
                delay(100)
            }
        }
    }

    if (showQrDialog) {
        val qrBitmap =
            generateQrBitmap(
                onQrDialogShow(node.route, listState.firstVisibleItemIndex),
            )
        QrDialog(qrBitmap) { showQrDialog = false }
    }

    LazyColumn(
        modifier =
            Modifier
                .padding(horizontal = if (isLandscape) 40.dp else 20.dp)
                .pointerInput(Unit) { detectTapGestures { isVisible.value = !isVisible.value } },
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.padding(top = initialTopPadding.value))
        }
        items(prayers) { prayerElement ->
            PrayerElementRenderer(
                prayerElement,
                renderContext,
                currentFilename,
                onPrayerButtonClick,
            )
        }
        item {
            Spacer(Modifier.padding(bottom = initialBottomPadding.value))
        }
    }
}

data class PrayerRenderContext(
    val translations: Map<String, String>,
    val dynamicSongKey: String?,
    val isSongHorizontalScroll: Boolean,
    val onDynamicSongKeyChanged: (String) -> Unit,
    val onError: (String, String) -> Unit,
)

@Composable
fun PrayerElementRenderer(
    prayerElement: PrayerElement,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
) {
    when (prayerElement) {
        is PrayerElement.Title -> {
            Title(prayerElement.content)
        }

        is PrayerElement.Heading -> {
            Heading(prayerElement.content)
        }

        is PrayerElement.Subheading -> {
            Subheading(prayerElement.content)
        }

        is PrayerElement.Prose -> {
            Prose(prayerElement.content)
        }

        is PrayerElement.Song -> {
            Song(prayerElement.content, isHorizontal = context.isSongHorizontalScroll)
        }

        is PrayerElement.Subtext -> {
            Subtext(prayerElement.content)
        }

        is PrayerElement.Button -> {
            PrayerButton(
                prayerButton = prayerElement,
                onPrayerButtonClick = onPrayerButtonClick,
                translations = context.translations,
            )
        }

        is PrayerElement.Source -> {
            Source(prayerElement.content)
        }

        is PrayerElement.CollapsibleBlock -> {
            CollapsibleTextBlock(
                prayerElement,
                context,
                filename,
                onPrayerButtonClick,
            )
        }

        is PrayerElement.Error -> {
            ErrorBlock(
                "Error: ${prayerElement.content}",
                onError = context.onError,
                filename,
            )
        }

        is PrayerElement.DynamicSongsBlock -> {
            if (prayerElement.items.isNotEmpty()) {
                DynamicSongsBlockUI(
                    prayerElement,
                    context,
                    filename,
                    onPrayerButtonClick,
                )
            }
        }

        is PrayerElement.DynamicSong -> {
            DynamicSongUI(
                prayerElement,
                context,
                filename,
                onPrayerButtonClick,
            )
        }

        is PrayerElement.AlternativePrayersBlock -> {
            AlternativePrayersUI(
                prayerElement,
                context,
                filename,
                onPrayerButtonClick,
            )
        }

        is PrayerElement.Link -> {
            // This block indicates that a 'Link' element unexpectedly reached the UI.
            // Log an error or render a debug message, as it should ideally not happen.
            ErrorBlock(
                "UI Error: Unresolved Link element encountered",
                context.onError,
                filename,
            )
        }

        is PrayerElement.LinkCollapsible -> {
            // Similar to 'Link', this suggests an issue in the data resolution layer.
            ErrorBlock(
                "UI Error: Unresolved LinkCollapsible element encountered",
                context.onError,
                filename,
            )
        }

        is PrayerElement.AlternativeOption -> {
            ErrorBlock(
                "UI Error: AlternativeOption element encountered outside of AlternativePrayersBlock",
                context.onError,
                filename,
            )
        }
    }
}

@Composable
fun PrayerButton(
    prayerButton: PrayerElement.Button,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    translations: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    val displayText: String =
        prayerButton
            .label
            ?: prayerButton
                .link
                .split("_")
                .mapNotNull { word -> translations[word] }
                .joinToString(" ")
                .ifEmpty { "Error" }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = {
                onPrayerButtonClick(prayerButton.link, prayerButton.replace)
            },
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier.padding(vertical = 8.dp),
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to $displayText",
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSongsBlockUI(
    dynamicSongsBlock: PrayerElement.DynamicSongsBlock,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dynamicSongKey = context.dynamicSongKey

    val dynamicSong =
        dynamicSongsBlock.items.find { it.eventKey == dynamicSongKey }
            ?: dynamicSongsBlock.items.firstOrNull()
    // For dropdown menu
    val songs = dynamicSongsBlock.items
    var expanded by remember { mutableStateOf(false) }

    val titles =
        songs.map { song ->
            song.eventTitle
        }
    val selectedTitle = dynamicSong?.eventTitle ?: "Error"
    Card(modifier) {
        Column(Modifier.padding(4.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        value = selectedTitle,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            if (titles.size > 1) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        modifier =
                            Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        songs.forEach { song ->
                            DropdownMenuItem(
                                text = { Text(song.eventTitle) },
                                onClick = {
                                    context.onDynamicSongKeyChanged(song.eventKey)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }

            if (dynamicSong != null) {
                DynamicSongUI(
                    dynamicSong,
                    context,
                    filename,
                    onPrayerButtonClick,
                )
            }
        }
    }
}

@Composable
fun DynamicSongUI(
    dynamicSong: PrayerElement.DynamicSong,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        dynamicSong.items.forEach { item ->
            when (item) {
                is PrayerElement.Song,
                is PrayerElement.Subheading,
                is PrayerElement.CollapsibleBlock,
                is PrayerElement.AlternativePrayersBlock,
                is PrayerElement.AlternativeOption,
                -> {
                    PrayerElementRenderer(
                        item,
                        context,
                        filename,
                        onPrayerButtonClick,
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun CollapsibleTextBlock(
    prayerElement: PrayerElement.CollapsibleBlock,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
        ) {
            Heading(
                text = prayerElement.title,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Column {
                    Spacer(Modifier.padding(8.dp))
                    prayerElement.items.forEach { nestedItem ->
                        // Loop through type-safe items
                        // Recursively call the renderer for nested items
                        PrayerElementRenderer(
                            nestedItem,
                            context,
                            filename,
                            onPrayerButtonClick,
                        )
                        Spacer(Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}
