package com.paradox543.malankaraorthodoxliturgica.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

/**
 * Remembers a boolean visibility flag and a [androidx.compose.ui.input.nestedscroll.NestedScrollConnection] that hides bars
 * when scrolling down and reveals them when scrolling up.
 * Used by PrayerScreen and BibleChapterScreen.
 */
@Composable
fun rememberScrollAwareVisibility(): Pair<MutableState<Boolean>, NestedScrollConnection> {
    val isVisible = remember { mutableStateOf(true) }

    val nestedScrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (available.y > 50) {
                        isVisible.value =
                            true  // Scrolling UP → Show bars (50px threshold avoids inertia bounces)
                    } else if (available.y < -10) {
                        isVisible.value = false // Scrolling DOWN → Hide bars
                    }
                    return Offset.Companion.Zero
                }
            }
        }
    return isVisible to nestedScrollConnection
}