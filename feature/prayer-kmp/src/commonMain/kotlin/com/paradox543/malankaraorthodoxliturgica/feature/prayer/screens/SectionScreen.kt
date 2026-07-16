package com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Arrow_forward
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.Res
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.greatlent
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.painterResource

@Composable
fun SectionScreen(
    prayerViewModel: PrayerViewModel,
    prayerNavViewModel: PrayerNavViewModel,
    node: PageNode,
    contentPadding: PaddingValues,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
    onSectionNavigate: (String) -> Unit = {},
    onPrayerNavigate: (String) -> Unit = {},
    onSongNavigate: (String) -> Unit = {},
    onPrayNowNavigate: () -> Unit = {},
    topRecommendedPrayer: PageNode? = null,
    liturgicalDay: LiturgicalDay? = null,
) {
    val translations by prayerViewModel.translations.collectAsState()
    val nodes = node.children
    var title = ""
    for (item in node.route.split("_")) {
        title += (translations[item] ?: item) + " "
    }

    LaunchedEffect(title) { onScaffoldStateChanged(ScaffoldUiState.Standard(title)) }

    LaunchedEffect(Unit) {
        prayerNavViewModel.onSectionScreenOpened()
    }

    LaunchedEffect(Unit) {
        prayerNavViewModel.requestReview.collectLatest {
            prayerNavViewModel.checkForReview()
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
                    if (node.route == "malankara") {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            PrayNowHeroCard(
                                topRecommendedPrayer,
                                liturgicalDay,
                                translations,
                                onPrayerNavigate,
                                onPrayNowNavigate,
                            )
                        }
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
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        DisplayIconography("column")
                    }
                    if (node.route == "malankara") {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            PrayNowHeroCard(
                                topRecommendedPrayer,
                                liturgicalDay,
                                translations,
                                onPrayerNavigate,
                                onPrayNowNavigate,
                            )
                        }
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

@Composable
private fun PrayNowHeroCard(
    topPrayer: PageNode?,
    liturgicalDay: LiturgicalDay?,
    translations: Map<String, String>,
    onPrayerNavigate: (String) -> Unit,
    onPrayNowNavigate: () -> Unit,
) {
    if (topPrayer == null) return

    val season = liturgicalDay?.season
    val tune = liturgicalDay?.tune

    val routeParts = topPrayer.route.split("_")
    val prayerTitle =
        routeParts.joinToString(" ") { part ->
            if (part.contains("ragam")) {
                translations["ragam"] + " " + part.substringAfter("ragam")
            } else {
                translations[part] ?: part
            }
        }
    val seasonTitle = translations[season] ?: season ?: ""

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onPrayerNavigate(topPrayer.route) },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prayerTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    if (seasonTitle.isNotEmpty()) {
                        Text(
                            text = seasonTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        )
                    }
                }

                if (tune != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(
                            text = (translations["tune"] ?: "Tune") + " $tune",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onPrayNowNavigate) {
                    Text(
                        text = translations["otherRecommendedPrayers"] ?: "Other appropriate prayers",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = MaterialIcons.Rounded.Arrow_forward,
                        contentDescription = "See full list",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}
