package com.paradox543.malankaraorthodoxliturgica.qr

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun QrScannerOverlay(
    modifier: Modifier = Modifier,
    isDetected: Boolean
) {
    // Animate color change when QR detected
    val borderColor by animateColorAsState(
        targetValue = if (isDetected) Color(0xFF4CAF50) else Color.White, // green on success
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val overlaySize = 250.dp.toPx()
            val cornerLength = 40.dp.toPx()
            val strokeWidth = 6.dp.toPx()
            val w = size.width
            val h = size.height
            val left = (w - overlaySize) / 2
            val top = (h - overlaySize) / 2
            val right = left + overlaySize
            val bottom = top + overlaySize

            // Dim background with transparent cutout in center
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size,
                blendMode = BlendMode.SrcOver
            )
            drawRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(overlaySize, overlaySize),
                blendMode = BlendMode.Clear
            )

            // Draw 4 L-shaped corners
            // Top-left
            drawLine(borderColor, start = Offset(left, top), end = Offset(left + cornerLength, top), strokeWidth)
            drawLine(borderColor, start = Offset(left, top), end = Offset(left, top + cornerLength), strokeWidth)

            // Top-right
            drawLine(borderColor, start = Offset(right, top), end = Offset(right - cornerLength, top), strokeWidth)
            drawLine(borderColor, start = Offset(right, top), end = Offset(right, top + cornerLength), strokeWidth)

            // Bottom-left
            drawLine(borderColor, start = Offset(left, bottom), end = Offset(left + cornerLength, bottom), strokeWidth)
            drawLine(borderColor, start = Offset(left, bottom), end = Offset(left, bottom - cornerLength), strokeWidth)

            // Bottom-right
            drawLine(borderColor, start = Offset(right, bottom), end = Offset(right - cornerLength, bottom), strokeWidth)
            drawLine(borderColor, start = Offset(right, bottom), end = Offset(right, bottom - cornerLength), strokeWidth)
        }
    }
}
