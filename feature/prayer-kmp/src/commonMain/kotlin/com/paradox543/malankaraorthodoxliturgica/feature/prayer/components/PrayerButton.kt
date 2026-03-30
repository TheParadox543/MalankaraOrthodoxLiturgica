package com.paradox543.malankaraorthodoxliturgica.feature.prayer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Keyboard_arrow_right
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement

@Composable
fun PrayerButton(
    prayerButton: PrayerElement.Button,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    translations: Map<String, String>,
    modifier: Modifier = Modifier,
) {
    val displayText: String =
        prayerButton
            .label
            ?: prayerButton
                .link
                .split("_")
                .mapNotNull { word -> translations[word] }
                .joinToString(" ")
                .ifEmpty { "Error" }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Button(
            onClick = {
                onPrayerButtonClick(prayerButton.link, prayerButton.replace)
            },
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier.weight(1f).padding(vertical = 8.dp),
            )
            Icon(
                MaterialIcons.Rounded.Keyboard_arrow_right,
                contentDescription = "Go to $displayText",
            )
        }
    }
}