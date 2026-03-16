package com.paradox543.malankaraorthodoxliturgica.core.ui.modifier

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput

@Stable
data class PinchZoomConfig(
    val zoomInThreshold: Float = 1.2f,
    val zoomOutThreshold: Float = 0.8f,
)

fun Modifier.globalPinchZoom(
    enabled: Boolean,
    config: PinchZoomConfig = PinchZoomConfig(),
    onZoomInStep: () -> Unit,
    onZoomOutStep: () -> Unit,
): Modifier =
    composed {
        if (!enabled) return@composed this

        var cumulativeZoom by remember { mutableFloatStateOf(1f) }

        pointerInput(config, true) {
            detectTransformGestures { _, _, zoom, _ ->
                cumulativeZoom *= zoom
                when {
                    cumulativeZoom >= config.zoomInThreshold -> {
                        onZoomInStep()
                        cumulativeZoom = 1f
                    }

                    cumulativeZoom <= config.zoomOutThreshold -> {
                        onZoomOutStep()
                        cumulativeZoom = 1f
                    }
                }
            }
        }
    }