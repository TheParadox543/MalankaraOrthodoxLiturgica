package com.paradox543.malankaraorthodoxliturgica.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun RestoreTimePicker(
    onConfirm: (minutes: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val hourList =
        buildList {
            repeat(2) { add("") } // Add 2 empty slots at the start
            addAll((0..6).map { it.toString().padStart(2, '0') })
            repeat(2) { add("") } // Add 2 empty slots at the end
        }

    val minList =
        buildList {
            repeat(2) { add("") }
            repeat(6) {
                addAll((5..60 step 5).map { (it % 60).toString().padStart(2, '0') })
            }
            repeat(2) { add("") }
        }

    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val minState = rememberLazyListState(initialFirstVisibleItemIndex = 5)
    var hourIndex by remember { mutableIntStateOf(hourState.firstVisibleItemIndex) }
    var minuteIndex by remember { mutableIntStateOf(minState.firstVisibleItemIndex) }

    LaunchedEffect(hourState.firstVisibleItemIndex) {
        hourIndex = hourState.firstVisibleItemIndex
        if (minuteIndex <= (hourIndex * 12) || minuteIndex >= ((hourIndex + 1) * 12)) {
            minuteIndex = (hourIndex * 12) + (minuteIndex % 12)
        }
        minState.scrollToItem(minuteIndex)
        Log.d("RestoreTimePicker", "Time indices for hour: $hourIndex $minuteIndex")
    }
    LaunchedEffect(minState.firstVisibleItemIndex) {
        minuteIndex = minState.firstVisibleItemIndex
        hourIndex = (minuteIndex + 1) / 12
        hourState.animateScrollToItem(hourIndex)
        Log.d("RestoreTimePicker", "Time indices for minute: $hourIndex $minuteIndex")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Restore Time", Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        text = {
            Column {
                Text(
                    "$hourIndex hr ${if ((minuteIndex + 1) % 12 != 0) ((minuteIndex % 12 + 1) * 5) else 0} min",
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier =
                        Modifier
//                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TimePickerColumn(hourList, hourState, label = "hr")
                    TimePickerColumn(minList, minState, label = "min")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedMin = minState.firstVisibleItemIndex.coerceIn(0, minList.lastIndex)
                onConfirm((selectedMin + 1) * 5)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun TimePickerColumn(
    list: List<String>,
    scrollState: LazyListState,
    label: String,
) {
    val flingBehavior =
        rememberSnapFlingBehavior(
            scrollState,
            snapPosition = SnapPosition.Start,
        )
    val derivedState = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.isScrollInProgress }
            .collect { isScrolling ->
                if (!isScrolling) {
                    scrollState.animateScrollToItem(scrollState.firstVisibleItemIndex, 0)
                }
            }
    }
    Column(Modifier.padding(horizontal = 8.dp)) {
        Text(label, Modifier.width(80.dp), textAlign = TextAlign.Center)
        Box(
            modifier =
                Modifier
                    .height(200.dp)
                    .width(80.dp),
        ) {
            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                flingBehavior = flingBehavior,
            ) {
                itemsIndexed(list) { index, item ->
                    // distance from center
                    val distance = abs(derivedState.value - index + 2)
                    val scale = 1f - distance * 0.2f
                    val alpha = 1f - distance * 0.3f

                    Text(
                        text = item,
                        fontSize = (24.sp * scale),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .padding(vertical = 6.dp)
                                .height(28.dp)
                                .fillMaxWidth(),
                    )
                }
            }
            // highlight center line
            Box(
                Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                    ),
            )
        }
    }
}

@Preview
@Composable
fun RestoreTimePickerPreview() {
    RestoreTimePicker({ minutes -> }, {})
}