package com.paradox543.malankaraorthodoxliturgica.view

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.navigation.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.qr.QrFabGenerate
import com.paradox543.malankaraorthodoxliturgica.qr.QrFabScan
import com.paradox543.malankaraorthodoxliturgica.ui.components.Heading
import com.paradox543.malankaraorthodoxliturgica.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.ui.components.Song
import com.paradox543.malankaraorthodoxliturgica.ui.components.Source
import com.paradox543.malankaraorthodoxliturgica.ui.components.Subheading
import com.paradox543.malankaraorthodoxliturgica.ui.components.Subtext
import com.paradox543.malankaraorthodoxliturgica.ui.components.Title
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PrayerScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    settingsViewModel: SettingsViewModel,
    navViewModel: NavViewModel,
    node: PageNode,
    scrollIndex: Int = 0,
) {
    val prayers by prayerViewModel.prayers.collectAsState()
    val translations by prayerViewModel.translations.collectAsState()
    val songScrollState by settingsViewModel.songScrollState.collectAsState()
    var title = ""
    for (item in node.route.split("_")) {
        title += translations[item] + " "
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val (isVisible, nestedScrollConnection) = rememberScrollAwareVisibility()

    val currentFilename = node.filename ?: "NoFileNameFound"
    val (prevNodeRoute, nextNodeRoute) = navViewModel.getAdjacentSiblingRoutes(node)

    // State to accumulate zoom gesture delta for triggering discrete steps
    var cumulativeZoomFactor by remember { mutableFloatStateOf(1f) }

    // Define thresholds for triggering a step up/down (adjust these for sensitivity)
    val zoomInThreshold = 1.2f  // If accumulated zoom factor exceeds this, step up
    val zoomOutThreshold = 0.8f // If accumulated zoom factor falls below this, step down

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

    Scaffold(
        modifier =
            Modifier
                .nestedScroll(nestedScrollConnection)
                .pointerInput(Unit) { detectTapGestures { isVisible.value = !isVisible.value } }
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        cumulativeZoomFactor *= zoom

                        if (cumulativeZoomFactor >= zoomInThreshold) {
                            settingsViewModel.stepFontScale(1)
                            cumulativeZoomFactor = 1f
                        } else if (cumulativeZoomFactor <= zoomOutThreshold) {
                            settingsViewModel.stepFontScale(-1)
                            cumulativeZoomFactor = 1f
                        }
                    }
                },
        topBar = {
            AnimatedVisibility(
                visible = isVisible.value,
                modifier = Modifier.zIndex(1f),
            ) {
                TopNavBar(
                    title,
                    navController,
                ) { navController.navigate(Screen.Settings.route) }
            }
        },
        bottomBar = {
            if (prevNodeRoute != null || nextNodeRoute != null) {
                AnimatedVisibility(
                    visible = isVisible.value,
                    modifier = Modifier.zIndex(1f),
                ) {
                    SectionNavBar(navController, prevNodeRoute, nextNodeRoute)
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isVisible.value,
                enter = fadeIn(),
                exit = shrinkOut(),
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    QrFabScan(
                        navController,
                        Modifier
                            .scale(0.8f)
                            .padding(start = 12.dp),
                        false,
                    )
                    QrFabGenerate(
//                        Screen.Prayer.createDeepLink(node.route, listState.firstVisibleItemIndex),
                        routeProvider = {
                            Screen.Prayer.createDeepLink(
                                node.route,
                                listState.firstVisibleItemIndex,
                            )
                        },
                        Modifier.scale(1.2f),
                    )
                }
            }
        },
    ) { innerPadding ->

        // Capture the system window insets once when the composable is first launched
        LaunchedEffect(innerPadding) {
            if (initialTopPadding.value == 0.dp) {
                initialTopPadding.value = innerPadding.calculateTopPadding()
            }
            if (initialBottomPadding.value == 0.dp) {
                initialBottomPadding.value = innerPadding.calculateBottomPadding()
            }
        }
        LaunchedEffect(Unit) {
            var retryCount = 0
            if (scrollIndex > 0) {
                while (listState.firstVisibleItemIndex != scrollIndex && retryCount < 10) {
                    Log.d("QR in Prayer Screen", "Detected scroll from Qr: $scrollIndex")
                    listState.scrollToItem(scrollIndex)
                    Log.d("QR in Prayer Screen", "Scrolled to item: ${listState.firstVisibleItemIndex}")
                    retryCount++
                    delay(100) // Small delay to allow UI to update
                }
            }
        }

        LazyColumn(
            modifier =
                Modifier
                    .padding(horizontal = if (isLandscape) 40.dp else 20.dp),
//                    .fillMaxWidth(if (isLandscape) 0.8f else 1f), // Limit width in landscape
            state = listState,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Spacer(Modifier.padding(top = initialTopPadding.value))
            }
            items(prayers) { prayerElement ->
                PrayerElementRenderer(
                    prayerElement,
                    prayerViewModel,
                    currentFilename,
                    navController,
                    songScrollState,
                )
            }
            item {
                Spacer(Modifier.padding(bottom = initialBottomPadding.value))
            }
        }
    }
}

@Composable
fun PrayerElementRenderer(
    prayerElement: PrayerElement,
    prayerViewModel: PrayerViewModel,
    filename: String,
    navController: NavController,
    isSongHorizontalScroll: Boolean = false,
) {
    val translations by prayerViewModel.translations.collectAsState()
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
            Song(prayerElement.content, isHorizontal = isSongHorizontalScroll)
        }

        is PrayerElement.Subtext -> {
            Subtext(prayerElement.content)
        }

        is PrayerElement.Button -> {
            PrayerButton(
                prayerButton = prayerElement,
                navController = navController,
                translations = translations,
            )
        }

        is PrayerElement.Source -> {
            Source(prayerElement.content)
        }

        is PrayerElement.CollapsibleBlock -> {
            CollapsibleTextBlock(
                title = prayerElement.title,
            ) {
                Column {
                    Spacer(Modifier.padding(8.dp))
                    prayerElement.items.forEach { nestedItem ->
                        // Loop through type-safe items
                        // Recursively call the renderer for nested items
                        PrayerElementRenderer(
                            nestedItem,
                            prayerViewModel,
                            filename,
                            navController,
                        )
                        Spacer(Modifier.padding(4.dp))
                    }
                }
            }
        }

        is PrayerElement.Error -> {
            ErrorBlock(
                "Error: ${prayerElement.content}",
                prayerViewModel,
                filename,
            )
        }

        is PrayerElement.DynamicContent -> {
            DynamicContentUI(
                prayerElement,
                prayerViewModel,
                filename,
                navController,
                isSongHorizontalScroll,
            )
        }

        is PrayerElement.DynamicSong -> {
            DynamicSongUI(
                prayerElement,
                prayerViewModel,
                filename,
                navController,
                isSongHorizontalScroll,
            )
        }

        is PrayerElement.Link -> {
            // This block indicates that a 'Link' element unexpectedly reached the UI.
            // Log an error or render a debug message, as it should ideally not happen.
            ErrorBlock(
                "UI Error: Unresolved Link element encountered",
                prayerViewModel,
                filename,
            )
        }

        is PrayerElement.LinkCollapsible -> {
            // Similar to 'Link', this suggests an issue in the data resolution layer.
            ErrorBlock(
                "UI Error: Unresolved LinkCollapsible element encountered",
                prayerViewModel,
                filename,
            )
        }
    }
}

@Composable
fun PrayerButton(
    prayerButton: PrayerElement.Button,
    navController: NavController,
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
                navController.navigate(Screen.Prayer.createRoute(prayerButton.link)) {
                    if (prayerButton.replace) {
                        navController.popBackStack()
                    }
                }
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
fun DynamicContentUI(
    dynamicContent: PrayerElement.DynamicContent,
    prayerViewModel: PrayerViewModel,
    filename: String,
    navController: NavController,
    isSongHorizontalScroll: Boolean,
    modifier: Modifier = Modifier,
) {
    val dynamicSongIndex by prayerViewModel.dynamicSongIndex.collectAsState()
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()

    val dynamicSong = dynamicContent.items.getOrNull(dynamicSongIndex) ?: return
    // For dropdown menu
    val songs = dynamicContent.items
    var expanded by remember { mutableStateOf(false) }

    val titles =
        songs.map { song ->
            when (selectedLanguage) {
                AppLanguage.MALAYALAM -> song.eventTitle.ml ?: song.eventTitle.en
                else -> song.eventTitle.en
            }
        }
    val selectedTitle = titles.getOrNull(dynamicSongIndex) ?: ""
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
                        titles.forEachIndexed { index, title ->
                            DropdownMenuItem(
                                text = { Text(title) },
                                onClick = {
                                    prayerViewModel.setDynamicSongIndex(index)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }

            DynamicSongUI(
                dynamicSong,
                prayerViewModel,
                filename,
                navController,
                isSongHorizontalScroll,
            )
        }
    }
}

@Composable
fun DynamicSongUI(
    dynamicSong: PrayerElement.DynamicSong,
    prayerViewModel: PrayerViewModel,
    filename: String,
    navController: NavController,
    isSongHorizontalScroll: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        dynamicSong.items.forEach { item ->
            when (item) {
                is PrayerElement.Song,
                is PrayerElement.Subheading,
                is PrayerElement.CollapsibleBlock,
                -> {
                    PrayerElementRenderer(
                        item,
                        prayerViewModel,
                        filename,
                        navController,
                        isSongHorizontalScroll,
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ErrorBlock(
    text: String,
    prayerViewModel: PrayerViewModel,
    errorLocation: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.fillMaxWidth(),
    )
    prayerViewModel.handlePrayerElementError(text, errorLocation)
}

@Composable
fun CollapsibleTextBlock(
    title: String,
    content: @Composable () -> Unit, // Changed content to Composable Lambda
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun rememberScrollAwareVisibility(): Pair<MutableState<Boolean>, NestedScrollConnection> {
    val isVisible = remember { mutableStateOf(true) }

    val nestedScrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (available.y > 20) {
                        isVisible.value = true  // Scrolling UP → Show bars
                    } else if (available.y < 0) {
                        isVisible.value = false // Scrolling DOWN → Hide bars
                    }
                    return Offset.Zero
                }
            }
        }
    return isVisible to nestedScrollConnection
}