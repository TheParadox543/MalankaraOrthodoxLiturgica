package com.paradox543.malankaraorthodoxliturgica.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Song(
    text: String,
    modifier: Modifier = Modifier,
    isHorizontal: Boolean = false,
) {
    val horizontalScrollState = rememberScrollState()
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .let { currentModifier ->
                    if (isHorizontal) {
                        currentModifier
                            .horizontalScroll(horizontalScrollState)
                    } else {
                        currentModifier
                    }
                }.border(
                    4.dp,
                    MaterialTheme.colorScheme.outline,
                    MaterialTheme.shapes.medium,
                ),
    ) {
        Text(
            text = text.applyPrayerReplacements(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
        )
    }
}

private fun String.applyPrayerReplacements(): String =
    this
        .replace("/t", "    ")
        .replace("/u200b", "\u200b")