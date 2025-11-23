package com.paradox543.malankaraorthodoxliturgica.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerViewModel

@Composable
fun ErrorBlock(
    text: String,
    prayerViewModel: PrayerViewModel,
    errorLocation: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.error,
        modifier = modifier.fillMaxWidth(),
    )
    prayerViewModel.handlePrayerElementError(text, errorLocation)
}