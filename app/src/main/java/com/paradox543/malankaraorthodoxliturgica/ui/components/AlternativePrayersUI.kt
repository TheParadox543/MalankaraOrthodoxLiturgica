package com.paradox543.malankaraorthodoxliturgica.ui.components

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
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.ui.screens.PrayerElementRenderer
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlternativePrayersUI(
    element: PrayerElementDomain.AlternativePrayersBlock,
    prayerViewModel: PrayerViewModel,
    filename: String,
    navController: NavController,
    isSongHorizontalScroll: Boolean,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Column(
        Modifier.Companion.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Subheading(
            element.title,
            Modifier.Companion.padding(bottom = 8.dp),
        )

        SingleChoiceSegmentedButtonRow(Modifier.Companion.fillMaxWidth()) {
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

        Spacer(Modifier.Companion.height(8.dp))

        // Render the selected option's content
        element.options[selectedIndex].items.forEach { child ->
            if (child !is PrayerElementDomain.Heading) {
                PrayerElementRenderer(
                    prayerElementDomain = child,
                    prayerViewModel = prayerViewModel,
                    filename = filename,
                    navController = navController,
                    isSongHorizontalScroll = isSongHorizontalScroll,
                )
            }
        }
    }
}