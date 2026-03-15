package com.paradox543.malankaraorthodoxliturgica.feature.prayer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Subheading
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerElementRenderer
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerRenderContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlternativePrayersUI(
    element: PrayerElement.AlternativePrayersBlock,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Subheading(
            element.title,
            Modifier.padding(bottom = 8.dp),
        )

        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            element.options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = index == selectedIndex,
                    onClick = { selectedIndex = index },
                    shape =
                        SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = element.options.size,
                        ),
                ) {
                    Text(option.label)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Render the selected option's content
        element.options[selectedIndex].items.forEach { child ->
            if (child !is PrayerElement.Heading) {
                PrayerElementRenderer(
                    prayerElement = child,
                    context = context,
                    filename = filename,
                    onPrayerButtonClick = onPrayerButtonClick,
                )
            }
        }
    }
}