package com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.Res
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.greatlent
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource

@Composable
fun SectionScreen(
    prayerViewModel: PrayerViewModel,
    node: PageNode,
    contentPadding: PaddingValues,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
    onSectionNavigate: (String) -> Unit = {},
    onPrayerNavigate: (String) -> Unit = {},
    onSongNavigate: (String) -> Unit = {},
) {
    val translations by prayerViewModel.translations.collectAsState()
    val nodes = node.children
    var title = ""
    for (item in node.route.split("_")) {
        title += (translations[item] ?: item) + " "
    }

    LaunchedEffect(title) { onScaffoldStateChanged(ScaffoldUiState.Standard(title)) }

    LaunchedEffect(Unit) {
        prayerViewModel.onSectionScreenOpened()
    }

    LaunchedEffect(Unit) {
        prayerViewModel.requestReview.collectLatest {
            prayerViewModel.checkForReview()
        }
    }

    BoxWithConstraints {
        val width = maxWidth
        if (width > 600.dp) {
            Row(
                Modifier.padding(contentPadding),
            ) {
                DisplayIconography("row")
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(240.dp),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    items(nodes.size) { index ->
                        SectionCard(
                            nodes[index],
                            translations,
                            prayerViewModel::reportBrokenNavigation,
                            onSectionNavigate,
                            onPrayerNavigate,
                            onSongNavigate,
                        )
                    }
                }
            }
        } else {
            Column(
                Modifier.padding(contentPadding),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(240.dp),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                            .weight(0.6f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    item {
                        DisplayIconography("column")
                    }
                    items(nodes.size) { index ->
                        SectionCard(
                            nodes[index],
                            translations,
                            prayerViewModel::reportBrokenNavigation,
                            onSectionNavigate,
                            onPrayerNavigate,
                            onSongNavigate,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DisplayIconography(orientation: String) {
    Image(
        painter = painterResource(Res.drawable.greatlent),
        contentDescription = "icon",
        modifier =
            if (orientation == "row") {
                Modifier
                    .requiredWidthIn(min = 200.dp, max = 400.dp)
                    .fillMaxHeight()
            } else {
                Modifier
                    .requiredWidthIn(max = 400.dp)
            },
        alignment = Alignment.TopStart,
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun SectionCard(
    node: PageNode,
    translations: Map<String, String>,
    logError: (String) -> Unit,
    onSectionNavigate: (String) -> Unit,
    onPrayerNavigate: (String) -> Unit,
    onSongNavigate: (String) -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clickable {
                    val filename = node.filename
                    if (node.children.isNotEmpty()) {
                        onSectionNavigate(node.route)
                    } else if (filename != null && filename.endsWith(".json")) {
                        onPrayerNavigate(node.route)
                    } else if (node.type == "song" || (filename != null && filename.endsWith(".mp3"))) {
                        onSongNavigate(node.route)
                    } else {
                        logError(node.route)
                    }
                },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        val text = node.route.split("_").last()
        Text(
            text =
                if (text.contains("ragam")) {
                    translations["ragam"] + " " + text.substringAfter("ragam")
                } else {
                    translations[text] ?: text
                },
            style = MaterialTheme.typography.titleMedium,
            modifier =
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}
