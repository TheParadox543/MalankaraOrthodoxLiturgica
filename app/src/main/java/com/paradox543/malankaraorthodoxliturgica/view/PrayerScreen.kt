package com.paradox543.malankaraorthodoxliturgica.view

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.data.model.AppFontSize
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.navigation.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PrayerScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    settingsViewModel: SettingsViewModel,
    navViewModel: NavViewModel,
    node: PageNode
) {
    val prayers by prayerViewModel.prayers.collectAsState()
    val translations by prayerViewModel.translations.collectAsState()
    val selectedFontSize by settingsViewModel.selectedFontSize.collectAsState()
    val songScrollState by settingsViewModel.songScrollState.collectAsState()
    var title = ""
    for (item in node.route.split("_")){
        title += translations[item] + " "
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val (isVisible, nestedScrollConnection) = rememberScrollAwareVisibility()

    val currentFilename = node.filename?: "NoFileNameFound"
    val (prevNodeRoute, nextNodeRoute) = navViewModel.getAdjacentSiblingRoutes(node)

    // Ensure prayers are loaded only when filename changes
    LaunchedEffect(currentFilename) {
        prayerViewModel.loadPrayerElements(currentFilename)
        prayerViewModel.logPrayerScreenView(title, currentFilename)
    }

    // Store the initial system bar padding values
    val initialTopPadding = remember { mutableStateOf(0.dp) }
    val initialBottomPadding = remember { mutableStateOf(0.dp) }

    val listState = rememberSaveable(saver = LazyListState.Saver, key=currentFilename){
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
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .pointerInput(Unit) {
                detectTapGestures { isVisible.value = !isVisible.value }
            },
        topBar = {
            AnimatedVisibility(
                visible = isVisible.value,
                modifier = Modifier.zIndex(1f)
            ) {
                TopNavBar(
                    title,
                    navController,
                    onActionClick = { navController.navigate("settings") }
                )
            }
        },
        bottomBar = {
            if (prevNodeRoute != null || nextNodeRoute != null) {
                AnimatedVisibility(
                    visible = isVisible.value,
                    modifier = Modifier.zIndex(1f)
                ) {
                    SectionNavBar(navController, prevNodeRoute, nextNodeRoute)
                }
            }
        }
    ) { innerPadding ->

        // Capture the system window insets once when the composable is first launched
        LaunchedEffect(innerPadding) {
            if (initialTopPadding.value == 0.dp){
                initialTopPadding.value = innerPadding.calculateTopPadding()
            }
            if (initialBottomPadding.value == 0.dp) {
                initialBottomPadding.value = innerPadding.calculateBottomPadding()
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = if (isLandscape) 40.dp else 20.dp) // Reduce width in landscape
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(if (isLandscape) 0.8f else 1f), // Limit width in landscape
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.padding(top = initialTopPadding.value))
                }
                items(prayers) { prayerElement ->
                    PrayerElementRenderer(
                        prayerElement,
                        selectedFontSize,
                        prayerViewModel,
                        currentFilename,
                        songScrollState,
                    )
                }
                item {
                    Spacer(Modifier.padding(bottom = initialBottomPadding.value))
                }
            }
        }
    }
}

@Composable
fun PrayerElementRenderer(
    prayerElement: PrayerElement,
    selectedFontSize: AppFontSize,
    prayerViewModel: PrayerViewModel,
    filename: String,
    isSongHorizontalScroll: Boolean = false,
) {
    when (prayerElement) {
        is PrayerElement.Title -> {
            Title(
                text = prayerElement.content,
                fontSize = selectedFontSize.fontSize
            )
        }

        is PrayerElement.Heading -> {
            Heading(
                text = prayerElement.content,
                fontSize = selectedFontSize.fontSize
            )
        }

        is PrayerElement.Subheading -> {
            Subheading(
                text = prayerElement.content,
                fontSize = selectedFontSize.fontSize
            )
        }

        is PrayerElement.Prose -> {
            Prose(
                text = prayerElement.content,
                fontSize = selectedFontSize.fontSize
            )
        }

        is PrayerElement.Song -> {
            Song(
                text = prayerElement.content,
                fontSize = selectedFontSize.fontSize,
                isHorizontal = isSongHorizontalScroll
            )
        }

        is PrayerElement.Subtext -> {
            Subtext(
                text = prayerElement.content,
                fontSize = selectedFontSize.fontSize
            )
        }

        is PrayerElement.CollapsibleBlock -> {
            CollapsibleTextBlock(
                title = prayerElement.title,
                fontSize = selectedFontSize.fontSize,
            ) {
                Column {
                    Spacer(Modifier.padding(8.dp))
                    prayerElement.items.forEach { nestedItem -> // Loop through type-safe items
                        // Recursively call the renderer for nested items
                        PrayerElementRenderer(nestedItem, selectedFontSize, prayerViewModel, filename)
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
                fontSize = selectedFontSize.fontSize
            )
        }

        is PrayerElement.Link -> {
            // This block indicates that a 'Link' element unexpectedly reached the UI.
            // Log an error or render a debug message, as it should ideally not happen.
            ErrorBlock(
                "UI Error: Unresolved Link element encountered",
                prayerViewModel,
                filename,
                fontSize = selectedFontSize.fontSize
            )
        }

        is PrayerElement.LinkCollapsible -> {
            // Similar to 'Link', this suggests an issue in the data resolution layer.
            ErrorBlock(
                "UI Error: Unresolved LinkCollapsible element encountered",
                prayerViewModel,
                filename,
                fontSize = selectedFontSize.fontSize
            )
        }
    }
}

@Composable
fun Title(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize * 5 / 4,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun Heading(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize * 5 / 4,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
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
    )
}

@Composable
fun Prose(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text.replace("/t", "    "),
        fontSize = fontSize,
        textAlign = TextAlign.Justify,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun Song(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp, isHorizontal: Boolean = false) {
    val horizontalScrollState = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .let { currentModifier ->
                if (isHorizontal) {
                    currentModifier.horizontalScroll(horizontalScrollState)
                } else {
                    currentModifier
                }
            }
            .border(4.dp, MaterialTheme.colorScheme.outline)
    ) {
        Text(
            text = text.replace("/t", "    "),
            fontSize = fontSize,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun Subtext(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text,
        fontSize = fontSize,
        textAlign = TextAlign.End,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun ErrorBlock(
    text: String,
    prayerViewModel: PrayerViewModel,
    errorLocation: String,
    fontSize: TextUnit = AppFontSize.Medium.fontSize,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        fontSize = fontSize,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
            .fillMaxWidth()
    )
    prayerViewModel.handlePrayerElementError(text, errorLocation)
}

@Composable
fun CollapsibleTextBlock(
    title: String,
    fontSize: TextUnit = 16.sp,
    content: @Composable () -> Unit // Changed content to Composable Lambda
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            Text(
                text = title,
                fontSize = fontSize * 5 / 4,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column{
                content()
            }
        }
    }
}

@Composable
fun rememberScrollAwareVisibility(): Pair<MutableState<Boolean>, NestedScrollConnection> {
    val isVisible = remember { mutableStateOf(true) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
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