package com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Arrow_forward
import com.composables.icons.materialicons.rounded.Info
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.SeasonName
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.Res
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.annunciation
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.default
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.epiphany
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.great_lent
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.holy_cross
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.pentecost
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.resurrection
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.transfiguration
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.math.max

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
    onIndexNavigate: () -> Unit = {},
    topRecommendedPrayer: PageNode? = null,
    liturgicalDay: LiturgicalDay? = null,
) {
    val translations by prayerViewModel.translations.collectAsState()
    val nodes = node.children
    var title = ""
    for (item in node.route.split("_")) {
        title += (translations[item] ?: item) + " "
    }
    val displayIcon =
        when (liturgicalDay?.seasonName) {
            SeasonName.ANNUNCIATION -> Res.drawable.annunciation
            SeasonName.EPIPHANY -> Res.drawable.epiphany
            SeasonName.GREAT_LENT -> Res.drawable.great_lent
            SeasonName.RESURRECTION -> Res.drawable.resurrection
            SeasonName.PENTECOST -> Res.drawable.pentecost
            SeasonName.TRANSFIGURATION -> Res.drawable.transfiguration
            SeasonName.HOLY_CROSS -> Res.drawable.holy_cross
            SeasonName.DUMMY -> Res.drawable.default
            null -> Res.drawable.default
        }
    val listState = rememberLazyGridState()

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
                Modifier.padding(
                    top = contentPadding.calculateTopPadding(),
                    bottom = contentPadding.calculateBottomPadding(),
                    start = 8.dp,
                    end = 0.dp,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DisplayIconography(displayIcon, "row")
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(240.dp),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(start = 8.dp, end = 20.dp)
                            .verticalGridScrollbar(
                                state = listState,
                                isColumn = false,
                                isMalankaraRoot = node.route == "malankara",
                                itemCount = nodes.size,
                            ),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    state = listState,
                    contentPadding = PaddingValues(end = 8.dp),
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
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ListItem(
                                headlineContent = {
                                    Text(translations["indexOfPrayers"] ?: "Index of prayers")
                                },
                                modifier = Modifier.clickable(onClick = { onIndexNavigate() }),
                                leadingContent = {
                                    Icon(
                                        MaterialIcons.Rounded.Info,
                                        contentDescription = "Index of prayers",
                                    )
                                },
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
                            .weight(0.6f)
                            .verticalGridScrollbar(
                                state = listState,
                                isColumn = true,
                                isMalankaraRoot = node.route == "malankara",
                                itemCount = nodes.size,
                            ),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    state = listState,
                    contentPadding = PaddingValues(end = 8.dp),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        DisplayIconography(displayIcon, "column")
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
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ListItem(
                                headlineContent = {
                                    Text(translations["indexOfPrayers"] ?: "Index of prayers")
                                },
                                modifier = Modifier.clickable(onClick = { onIndexNavigate() }),
                                leadingContent = {
                                    Icon(
                                        MaterialIcons.Rounded.Info,
                                        contentDescription = "Index of prayers",
                                    )
                                },
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
private fun DisplayIconography(
    icon: DrawableResource,
    orientation: String,
) {
    if (orientation == "row") {
        Box(
            modifier = Modifier.width(200.dp).fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = "icon",
                modifier =
                    Modifier
                        .sizeIn(maxWidth = 200.dp, maxHeight = 200.dp)
                        .aspectRatio(1f),
                contentScale = ContentScale.Fit,
            )
        }
    } else {
        Image(
            painter = painterResource(icon),
            contentDescription = "icon",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .requiredWidthIn(max = 240.dp)
                    .aspectRatio(1f),
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit,
        )
    }
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

    val season = liturgicalDay?.seasonName
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
    val seasonTitle =
        translations[
            seasonTranslationKey(
                season ?: SeasonName.DUMMY,
            ),
        ] ?: season?.toDisplayName() ?: ""

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .clickable { onPrayerNavigate(topPrayer.route) },
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = prayerTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (seasonTitle.isNotEmpty()) {
                        Text(
                            text = seasonTitle,
                            style = MaterialTheme.typography.bodySmall,
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
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

private fun seasonTranslationKey(season: SeasonName): String =
    when (season) {
        SeasonName.TRANSFIGURATION -> "transfigurationSeason"
        else -> season.toString().lowercase()
    }

private fun SeasonName.toDisplayName(): String =
    toString()
        .lowercase()
        .replace(Regex("(?<=[a-z])(?=[A-Z])"), " ")
        .replaceFirstChar(Char::uppercase)

/**
 * A specialized vertical scrollbar for [LazyVerticalGrid] in [SectionScreen].
 * It draws a scrollbar thumb on the right side of the content.
 */
fun Modifier.verticalGridScrollbar(
    state: LazyGridState,
    isColumn: Boolean,
    isMalankaraRoot: Boolean,
    itemCount: Int,
    width: Dp = 4.dp,
    padding: Dp = 2.dp,
    color: Color? = null,
    alpha: Float = 0.4f,
): Modifier =
    composed {
        val scrollbarColor = color ?: MaterialTheme.colorScheme.onSurfaceVariant

        drawWithContent {
            drawContent()

            val layoutInfo = state.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo

            if (visibleItemsInfo.isEmpty() || (!state.canScrollForward && !state.canScrollBackward)) {
                return@drawWithContent
            }

            val viewportHeight = size.height

            // 1. Calculate individual component heights in pixels
            // If the item is visible, use its actual height for better accuracy
            fun getVisibleHeight(index: Int, defaultDp: Dp): Float {
                return visibleItemsInfo.find { it.index == index }?.size?.height?.toFloat() ?: defaultDp.toPx()
            }

            val iconHeight = if (isColumn) getVisibleHeight(0, 240.dp) else 0f
            var head = if (isColumn) 1 else 0
            
            val heroHeight = if (isMalankaraRoot) getVisibleHeight(head, 150.dp) else 0f
            if (isMalankaraRoot) head++
            
            val indexHeight = if (isMalankaraRoot) getVisibleHeight(head, 56.dp) else 0f
            if (isMalankaraRoot) head++

            // Estimate average card height from visible cards
            val visibleCards = visibleItemsInfo.filter { it.index >= head }
            val averageCardHeight = if (visibleCards.isNotEmpty()) {
                visibleCards.map { it.size.height }.average().toFloat()
            } else {
                90.dp.toPx()
            }

            // 2. Estimate total height
            // We need to know how many columns are there to estimate rows for cards
            val firstVisibleItem = visibleItemsInfo.first()
            val firstCardItem =
                visibleItemsInfo.firstOrNull {
                    it.index >= (if (isColumn) 1 else 0) + (if (isMalankaraRoot) 2 else 0)
                }

            // Try to find the number of items per row from currently visible items
            val itemsPerRow =
                if (firstCardItem != null) {
                    visibleItemsInfo.count { it.row == firstCardItem.row }
                } else {
                    1 // Fallback
                }

            val cardRows = (itemCount + itemsPerRow - 1) / max(1, itemsPerRow)
            val totalContentHeight = iconHeight + heroHeight + indexHeight + (cardRows * averageCardHeight)

            if (totalContentHeight <= viewportHeight) return@drawWithContent

            val thumbHeight =
                max(viewportHeight * viewportHeight / totalContentHeight, 24.dp.toPx())

            // 3. Calculate current scroll offset in pixels
            var currentScrollY = 0f
            val firstVisibleIndex = firstVisibleItem.index

            // Reset head for sequence logic
            var currentHead = 0
            if (isColumn) {
                if (firstVisibleIndex > currentHead) {
                    currentScrollY += iconHeight
                } else if (firstVisibleIndex == currentHead) {
                    currentScrollY += -firstVisibleItem.offset.y
                }
                currentHead++
            }
            if (isMalankaraRoot) {
                // Hero
                if (firstVisibleIndex > currentHead) {
                    currentScrollY += heroHeight
                } else if (firstVisibleIndex == currentHead) {
                    currentScrollY += -firstVisibleItem.offset.y
                }
                currentHead++
                // Index
                if (firstVisibleIndex > currentHead) {
                    currentScrollY += indexHeight
                } else if (firstVisibleIndex == currentHead) {
                    currentScrollY += -firstVisibleItem.offset.y
                }
                currentHead++
            }

            if (firstVisibleIndex >= currentHead) {
                val cardIndex = firstVisibleIndex - currentHead
                val currentRow = cardIndex / max(1, itemsPerRow)
                currentScrollY += (currentRow * averageCardHeight) + (-firstVisibleItem.offset.y)
            }

            val scrollProgress =
                (currentScrollY / (totalContentHeight - viewportHeight)).coerceIn(0f, 1f)
            val thumbOffset = (viewportHeight - thumbHeight) * scrollProgress

            drawRoundRect(
                color = scrollbarColor.copy(alpha = alpha),
                topLeft = Offset(size.width - width.toPx() - padding.toPx(), thumbOffset),
                size = Size(width.toPx(), thumbHeight),
                cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2),
            )
        }
    }
