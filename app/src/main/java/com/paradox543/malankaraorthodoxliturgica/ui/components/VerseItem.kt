package com.paradox543.malankaraorthodoxliturgica.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VerseItem(
    verseNumber: String,
    verseText: String,
) {
    Row {
        Text(
            text = verseNumber,
            modifier = Modifier.padding(4.dp).requiredWidth(20.dp),
            textAlign = TextAlign.Center,
        )
        Text(
            text = verseText,
            modifier = Modifier.padding(4.dp),
        )
    }
    HorizontalDivider()
}