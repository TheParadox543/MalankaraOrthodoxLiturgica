package com.paradox543.malankaraorthodoxliturgica.view

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.navigation.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.rememberScrollAwareVisibility
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
    node: PageNode,
    modifier: Modifier = Modifier
) {
    val prayers by prayerViewModel.prayers.collectAsState()
    val selectedFontSize by settingsViewModel.selectedFontSize.collectAsState()
    val translations by prayerViewModel.translations.collectAsState()
    var title = ""
    for (item in node.route.split("_")){
        title += translations[item] + " "
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val (isVisible, nestedScrollConnection) = rememberScrollAwareVisibility()

    val currentFilename = node.filename?: "NoFileNameFound"
    val (prevNodeRoute, nextNodeRoute) = navViewModel.getAdjacentSiblingRoutes(node)
    prayerViewModel.loadPrayers(currentFilename)

    val listState = rememberSaveable(saver = LazyListState.Saver, key=currentFilename){
        LazyListState()
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
            AnimatedVisibility(
                visible = isVisible.value,
                modifier = Modifier.zIndex(1f)
            ) {
                SectionNavBar(navController, prevNodeRoute, nextNodeRoute)
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isLandscape) 40.dp else 20.dp), // Reduce width in landscape
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(if (isLandscape) 0.8f else 1f) // Limit width in landscape
                    .fillMaxHeight(0.9f), // Limit height to avoid out of bounds
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(Modifier.padding(if (isLandscape) 40.dp else 32.dp))
                }
                items(prayers) { prayer ->
                    when (prayer["type"]) {
                        "title" -> {
                            Title(
                                text = (prayer["content"] ?: "").toString(),
                                fontSize = selectedFontSize
                            )
                        }

                        "heading" -> {
                            Heading(
                                text = (prayer["content"] ?: "").toString(),
                                fontSize = selectedFontSize
                            )
                        }

                        "subheading" -> {
                            Subheading(
                                text = (prayer["content"] ?: "").toString(),
                                fontSize = selectedFontSize
                            )
                        }

                        "prose" -> {
                            Prose(
                                text = (prayer["content"] ?: "").toString(),
                                fontSize = selectedFontSize
                            )
                        }

                        "song" -> {
                            Song(
                                text = (prayer["content"] ?: "").toString(),
                                fontSize = selectedFontSize
                            )
                        }

                        "subtext" -> {
                            Subtext(
                                text = (prayer["content"] ?: "").toString(),
                                fontSize = selectedFontSize
                            )
                        }

                        "collapsible-block" -> {
                            val collapsibleTitle = prayer["title"] as? String ?: "Expandable Section"
                            val items = prayer["items"] as? List<Map<String, String>> ?: emptyList()

                            CollapsibleTextBlock(
                                title = collapsibleTitle,
                                fontSize = selectedFontSize,
                            ){
                                Column {
                                    Spacer(Modifier.padding(8.dp))
                                    items.forEach {item ->
                                        when (item["type"]) {
                                            "heading" -> Heading(text = item["content"] ?: "", fontSize = selectedFontSize)
                                            "subheading" -> Subheading(text = item["content"] ?: "", fontSize = selectedFontSize)
                                            "prose" -> Prose(text = item["content"] ?: "", fontSize = selectedFontSize)
                                            "song" -> Song(text = item["content"] ?: "", fontSize = selectedFontSize)
                                            "subtext" -> Subtext(text = item["content"] ?: "", fontSize = selectedFontSize)
                                            else -> Text("Unknown collapsible item: ${item["type"]}", color=MaterialTheme.colorScheme.error)
                                        }
                                        Spacer(Modifier.padding(4.dp))
                                    }
                                }
                            }
                        }
                        "error" -> {
                            Text("Error: ${prayer["content"]}", color=MaterialTheme.colorScheme.error)
                        }

                        "newsection" -> {
                            Text("")
                        }
                        else -> {
                            Text("Unknown prayer element: ${prayer["type"]}", color=MaterialTheme.colorScheme.error)
                        }
                    }
                }
                item {
                    Spacer(Modifier.padding(if (isLandscape) 40.dp else 44.dp))
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
fun Song(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 16.sp) {
    Text(
        text = text.replace("/t", "    "),
        fontSize = fontSize,
        textAlign = TextAlign.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
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
    )
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