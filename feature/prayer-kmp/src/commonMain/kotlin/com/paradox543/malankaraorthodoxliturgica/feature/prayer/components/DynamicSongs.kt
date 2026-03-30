package com.paradox543.malankaraorthodoxliturgica.feature.prayer.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerElementRenderer
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerRenderContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSongsBlockUI(
    dynamicSongsBlock: PrayerElement.DynamicSongsBlock,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dynamicSongKey = context.dynamicSongKey

    val dynamicSong =
        dynamicSongsBlock.items.find { it.eventKey == dynamicSongKey }
            ?: dynamicSongsBlock.items.firstOrNull()
    // For dropdown menu
    val songs = dynamicSongsBlock.items
    var expanded by remember { mutableStateOf(false) }

    val titles =
        songs.map { song ->
            song.eventTitle
        }
    val selectedTitle = dynamicSong?.eventTitle ?: "Error"
    Card(modifier) {
        Column(
            Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        value = selectedTitle,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            if (titles.size > 1) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        modifier =
                            Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        songs.forEach { song ->
                            DropdownMenuItem(
                                text = { Text(song.eventTitle) },
                                onClick = {
                                    context.onDynamicSongKeyChanged(song.eventKey)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }

            if (dynamicSong != null) {
                DynamicSongUI(
                    dynamicSong,
                    context,
                    filename,
                    onPrayerButtonClick,
                )
            }
        }
    }
}

@Composable
fun DynamicSongUI(
    dynamicSong: PrayerElement.DynamicSong,
    context: PrayerRenderContext,
    filename: String,
    onPrayerButtonClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        dynamicSong.items.forEach { item ->
            when (item) {
                is PrayerElement.Song,
                is PrayerElement.Subheading,
                is PrayerElement.CollapsibleBlock,
                is PrayerElement.AlternativePrayersBlock,
                is PrayerElement.AlternativeOption,
                -> {
                    PrayerElementRenderer(
                        item,
                        context,
                        filename,
                        onPrayerButtonClick,
                    )
                }

                else -> {}
            }
        }
    }
}