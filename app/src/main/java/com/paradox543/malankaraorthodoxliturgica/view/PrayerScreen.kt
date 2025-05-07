package com.paradox543.malankaraorthodoxliturgica.view

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.paradox543.malankaraorthodoxliturgica.navigation.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.rememberScrollAwareVisibility
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PrayerScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel,
    modifier: Modifier = Modifier
) {
    val prayers by prayerViewModel.prayers.collectAsState()
    val language by prayerViewModel.selectedLanguage.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val currentSiblingIndex by navViewModel.currentSiblingIndex.collectAsState()
    val siblingNodes by navViewModel.siblingNodes.collectAsState()
    val (isVisible, nestedScrollConnection) = rememberScrollAwareVisibility()

    val currentFilename = siblingNodes[currentSiblingIndex!!].filename
    prayerViewModel.loadPrayers(currentFilename, language)
    prayerViewModel.setTopBarKeys(siblingNodes[currentSiblingIndex!!].route)

    val listState = rememberSaveable(saver = LazyListState.Saver, key=currentFilename){
        LazyListState()
    }
    val lastFilename = remember { mutableStateOf(currentFilename) }
    // Scroll to the top whenever currentSiblingIndex changes
    LaunchedEffect(currentFilename) {
        if (currentFilename != lastFilename.value) {
            listState.scrollToItem(0)
            lastFilename.value = currentFilename
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .pointerInput(Unit) {
                detectTapGestures { isVisible.value = !isVisible.value }
            },
        containerColor = Color.Transparent,
        topBar = {
            AnimatedVisibility(
                visible = isVisible.value,
                modifier = Modifier.zIndex(1f)
            ) {
                TopNavBar(
                    navController = navController,
                    prayerViewModel = prayerViewModel,
                    navViewModel = navViewModel,
                    onActionClick = { navController.navigate("settings") }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isVisible.value,
                modifier = Modifier.zIndex(1f)
            ) {
                SectionNavBar(
                    navController = navController,
                    navViewModel = navViewModel
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isLandscape) 80.dp else 8.dp), // Reduce width in landscape
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(if (isLandscape) 0.8f else 1f) // Limit width in landscape
                    .fillMaxHeight(if (isLandscape) 0.9f else 0.8f), // Limit height in portrait
                state = listState
            ) {
                items(prayers) { prayer ->
                    when (prayer["type"]) {
                        "title" -> Title(
                            text = prayer["content"] ?: "",
                            fontSize = selectedFontSize
                        )

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

                        "collapsible" -> CollapsibleTextBlock(
                            title = prayer["content"] ?: "",
                            content = "",
                            fontSize = selectedFontSize,
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
            }
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
            .padding(8.dp)
    )
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
        text = text.replace("/t", "    "),
        fontSize = fontSize,
        textAlign = TextAlign.Justify,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun CollapsibleTextBlock(
    title: String,
    content: String,
    fontSize: TextUnit = 16.sp
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
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
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }

        AnimatedVisibility(visible = expanded) {
            Text(
                text = content,
                fontSize = fontSize,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun Song(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text.replace("/t", "    "),
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