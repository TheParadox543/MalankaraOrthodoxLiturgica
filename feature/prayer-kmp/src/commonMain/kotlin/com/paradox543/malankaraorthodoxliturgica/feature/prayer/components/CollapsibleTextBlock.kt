package com.paradox543.malankaraorthodoxliturgica.feature.prayer.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Keyboard_arrow_down
import com.composables.icons.materialicons.rounded.Keyboard_arrow_up
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Heading
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerElementRenderer
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerRenderContext

@Composable
fun CollapsibleTextBlock(
    prayerElement: PrayerElement.CollapsibleBlock,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
        ) {
            Heading(
                text = prayerElement.title,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = if (expanded) MaterialIcons.Rounded.Keyboard_arrow_up else MaterialIcons.Rounded.Keyboard_arrow_down,
                contentDescription = if (expanded) "Collapse" else "Expand",
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                Column {
                    Spacer(Modifier.padding(8.dp))
                    prayerElement.items.forEach { nestedItem ->
                        // Loop through type-safe items
                        // Recursively call the renderer for nested items
                        PrayerElementRenderer(
                            nestedItem,
                            context,
                            filename,
                            onPrayerButtonClick,
                        )
                        Spacer(Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}