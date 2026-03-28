package com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Heading
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Song
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Source
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Subheading
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Subtext
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Title
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.rememberScrollAwareVisibility
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.components.AlternativePrayersUI
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.components.CollapsibleTextBlock
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.components.DynamicSongUI
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.components.DynamicSongsBlockUI
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.components.ErrorBlock
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.components.PrayerButton
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.qr.generation.QrDialog
import com.paradox543.malankaraorthodoxliturgica.qr.generation.generateQrMatrix
import com.paradox543.malankaraorthodoxliturgica.qr.generation.qrMatrixToImageBitmap
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
    routeProvider: (String) -> String,
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
                routeProvider = routeProvider,
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
                listState.scrollToItem(scrollIndex)
                retryCount++
                delay(100)
            }
        }
    }

    if (showQrDialog) {
        val matrix =
            generateQrMatrix(
                onQrDialogShow(node.route, listState.firstVisibleItemIndex),
            )
        val imageBitmap = qrMatrixToImageBitmap(matrix)
        QrDialog(imageBitmap) { showQrDialog = false }
    }

    BoxWithConstraints {
        val availableWidth = maxWidth
        LazyColumn(
            modifier =
                Modifier
                    .padding(horizontal = if (availableWidth > 600.dp) 40.dp else 20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            isVisible.value = !isVisible.value
                        }
                    },
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
