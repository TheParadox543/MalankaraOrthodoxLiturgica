package com.paradox543.malankaraorthodoxliturgica.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun Title(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Companion.Center,
        textDecoration = TextDecoration.Companion.Underline,
        modifier = modifier.fillMaxWidth(),
    )
}