package com.paradox543.malankaraorthodoxliturgica.view

import android.util.Log
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect // Don't forget this import!

@Composable
fun AutoFitZoomableSong(
    modifier: Modifier = Modifier,
    songContent: String, // The full string content of the song stanza
    defaultTextStyle: TextStyle, // The base style for your text
    onScaleChanged: (Float) -> Unit = {} // Callback to save the scale factor if desired
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    var contentWidth by remember { mutableFloatStateOf(0f) } // Measured width of the content without scaling
    var currentScale by remember { mutableFloatStateOf(1f) } // The current user-controlled scale
    var offsetX by remember { mutableFloatStateOf(0f) } // For horizontal panning
    var offsetY by remember { mutableFloatStateOf(0f) } // For vertical panning

    val minScale = 0.5f // Minimum zoom out
    val maxScale = 3f   // Maximum zoom in

    // Step 1: Measure the intrinsic width of the longest line
    LaunchedEffect(songContent, defaultTextStyle) {
        with(density) {
            var longestLinePx = 0f
            songContent.lines().forEach { line ->
                val result = textMeasurer.measure(
                    text = line,
                    style = defaultTextStyle,
                    constraints = Constraints(maxWidth = Int.MAX_VALUE) // Measure with infinite width
                )
                longestLinePx = maxOf(longestLinePx, result.size.width.toFloat())
            }
            contentWidth = longestLinePx
            // Optionally, calculate an initial scale here if longestLinePx > screenWidth
            // val initialScale = (density.density * LocalConfiguration.current.screenWidthDp).coerceAtLeast(1f) / longestLinePx
            // currentScale = initialScale.coerceIn(minScale, maxScale)
        }
    }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    // Apply gesture detection, graphicsLayer, and scroll modifiers to the outer Box
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    currentScale = (currentScale * zoom).coerceIn(minScale, maxScale)

                    val viewWidthPx = this.size.width.toFloat()
                    val viewHeightPx = this.size.height.toFloat()

                    val scaledContentWidth = contentWidth * currentScale
                    val scaledContentHeight = with(verticalScrollState) { (maxValue + viewportSize).toFloat() * currentScale }

                    offsetX = (offsetX + pan.x * currentScale) // Adjust pan based on current zoom
                    offsetY = (offsetY + pan.y * currentScale)

                    // Clamp offsets
                    offsetX = offsetX.coerceIn(
                        -maxOf(0f, scaledContentWidth - viewWidthPx) / 2f,
                        maxOf(0f, scaledContentWidth - viewWidthPx) / 2f
                    )
                    offsetY = offsetY.coerceIn(
                        -maxOf(0f, scaledContentHeight - viewHeightPx) / 2f,
                        maxOf(0f, scaledContentHeight - viewHeightPx) / 2f
                    )

                    onScaleChanged(currentScale)
                }
            }
            .graphicsLayer { // <--- THIS IS THE CHANGE: Use the lambda block here
                scaleX = currentScale
                scaleY = currentScale
                translationX = offsetX
                translationY = offsetY
                // Pivot fractions define the point around which scaling and rotation happen (0.5f, 0.5f is center)
//                pivotFractionX = 0.5f
//                pivotFractionY = 0.5f
            }
            // Apply scroll modifiers *after* scaling/translation, so the scroll happens on the transformed content
            .verticalScroll(verticalScrollState)
            .horizontalScroll(horizontalScrollState)

    ) {
        // The Text itself should simply occupy the space it needs within the scaled and scrollable Box.
        // No fillMaxSize() or scroll modifiers on this Column/Text directly.
        // The scaling is handled by graphicsLayer on the parent Box.
        Text(
            text = songContent,
            style = defaultTextStyle // Use the base style; graphicsLayer handles visual scaling
        )
    }
}