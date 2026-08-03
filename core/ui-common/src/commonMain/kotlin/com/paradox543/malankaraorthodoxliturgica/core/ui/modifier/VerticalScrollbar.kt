package com.paradox543.malankaraorthodoxliturgica.core.ui.modifier

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * A simple vertical scrollbar for [LazyColumn].
 * It draws a scrollbar thumb on the right side of the content.
 */
fun Modifier.verticalScrollbar(
    state: LazyListState,
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
            val totalItemsCount = layoutInfo.totalItemsCount

            if (visibleItemsInfo.isEmpty() || (!state.canScrollForward && !state.canScrollBackward)) {
                return@drawWithContent
            }

            val viewportHeight = size.height
            val firstVisibleItem = visibleItemsInfo.first()

            // Calculate thumb height proportionally to visible items vs total items
            val totalContentHeight =
                visibleItemsInfo.sumOf { it.size } *
                    totalItemsCount.toFloat() /
                    visibleItemsInfo.size

            val thumbHeight =
                max(viewportHeight * viewportHeight / totalContentHeight, 24.dp.toPx())

            val averageItemHeight = firstVisibleItem.size.toFloat()
            // Calculate scroll progress based on the first visible item index
            // We use totalItemsCount - visibleItemsInfo.size to avoid division by zero

            val scrollProgress =
                (
                    (
                        firstVisibleItem.index +
                            (-firstVisibleItem.offset / averageItemHeight)
                    ) / (totalItemsCount - visibleItemsInfo.size)
                ).coerceIn(0f, 1f)

            val thumbOffset = (viewportHeight - thumbHeight) * scrollProgress

            drawRoundRect(
                color = scrollbarColor.copy(alpha = alpha),
                topLeft = Offset(size.width - width.toPx() - padding.toPx(), thumbOffset),
                size = Size(width.toPx(), thumbHeight),
                cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2),
            )
        }
    }
