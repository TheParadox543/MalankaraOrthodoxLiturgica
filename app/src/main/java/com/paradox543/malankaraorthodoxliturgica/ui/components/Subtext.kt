package com.paradox543.malankaraorthodoxliturgica.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.paradox543.malankaraorthodoxliturgica.utils.applyPrayerReplacements

@Composable
fun Subtext(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.applyPrayerReplacements(),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Companion.End,
        modifier = modifier.fillMaxWidth(),
    )
}