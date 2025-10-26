package com.paradox543.malankaraorthodoxliturgica.ui.components

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

//  @OptIn(ExperimentalMaterial3Api::class)
//  @Composable
//  fun RestoreTimePicker() {
//    val minList = (0..55 step 5).map { if (it < 10) "0$it" else "$it" }
//    val hourList = (0..6).map { if (it < 10) "0$it" else "$it" }
//    var minIndex by remember { mutableIntStateOf(0) }
//    var hourIndex by remember { mutableIntStateOf(0) }
//    AlertDialog(
//        onDismissRequest = {},
//        title = { Text("Select Restore Time") },
//        text = {
//            Row {
//                LazyColumn(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
//                    itemsIndexed(hourList) { index, item ->
//                        if (index <= 4) {
//                            Text(
//                                item,
//                                Modifier
//                                    .width(80.dp)
//                                    .padding(vertical = 8.dp),
//                                fontSize = 24.sp * (1 - abs(2 - index) * 0.3),
//                                textAlign = TextAlign.Center,
//                            )
//                        }
//                    }
//                }
//                LazyColumn(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
//                    itemsIndexed(minList) { index, item ->
//                        if (index <= 4) {
//                            Text(
//                                item,
//                                Modifier
//                                    .width(80.dp)
//                                    .padding(vertical = 8.dp),
//                                fontSize = 24.sp * (1 - abs(2 - index) * 0.3),
//                                textAlign = TextAlign.Center,
//                            )
//                        }
//                    }
//                }
//            }
//        },
//        confirmButton = { Text("Confirm") },
//        dismissButton = { Text("Cancel") },
//    )
// }

@Composable
fun RestoreTimePicker(
    onConfirm: (hours: Int, minutes: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val minList = (0..55 step 5).map { it.toString().padStart(2, '0') }
    val hourList =
        (-2..8).map {
            if (it < 0 || it > 6) {
                ""
            } else {
                it.toString().padStart(2, '0')
            }
        }

    val useMinList = List(100) { minList }.flatten()

    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val minState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Restore Time", Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
        text = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                TimePickerColumn(hourList, hourState, label = "hr")
                TimePickerColumn(useMinList, minState, label = "min")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedHour = hourState.firstVisibleItemIndex.coerceIn(0, hourList.lastIndex)
                val selectedMin = minState.firstVisibleItemIndex.coerceIn(0, minList.lastIndex)
                onConfirm(selectedHour, selectedMin * 5)
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
    label: String
) {
    val flingBehavior =
        rememberSnapFlingBehavior(
            scrollState,
            snapPosition = SnapPosition.Start,
        )
    val derivedState = remember { derivedStateOf { scrollState.firstVisibleItemIndex } }
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
                    val distance = abs(derivedState.value - index)
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
    RestoreTimePicker({ hours, minutes -> }, {})
}