package com.paradox543.malankaraorthodoxliturgica.feature.prayer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.modifier.verticalScrollbar
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PageNode
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

    fun resolveDisplayTitle(song: PrayerElement.DynamicSong): String =
        context.translations[song.eventKey]
            ?: context.translations[song.eventKey.removeSuffix("Songs")]
            ?: song.eventTitle

    var expanded by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        SpecialSongSelectionDialog(
            context = context,
            onDismiss = { showAddDialog = false },
            onEventSelected = { key, title ->
                context.onAddManualDynamicSong(key, title, dynamicSongsBlock.timeKey)
                showAddDialog = false
            },
        )
    }

    val selectedTitle = dynamicSong?.let { resolveDisplayTitle(it) } ?: "Error"
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
                            if (songs.size > 1) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        },
                        modifier =
                            Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        songs.forEach { song ->
                            DropdownMenuItem(
                                text = { Text(resolveDisplayTitle(song)) },
                                onClick = {
                                    context.onDynamicSongKeyChanged(song.eventKey)
                                    expanded = false
                                },
                            )
                        }
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("+ Add item") },
                            onClick = {
                                showAddDialog = true
                                expanded = false
                            },
                        )
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
fun SpecialSongSelectionDialog(
    context: PrayerRenderContext,
    onDismiss: () -> Unit,
    onEventSelected: (String, String) -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val nodes = context.manualOptions
    val translations = context.translations

    fun resolveNodeTitle(node: PageNode): String {
        val lastPart = node.route.split("_").last()
        return translations[lastPart] ?: translations[node.route] ?: lastPart
    }

    val filteredNodes =
        remember(nodes, searchQuery, translations) {
            val query = searchQuery.trim().lowercase()
            if (query.isEmpty()) {
                nodes
            } else {
                nodes.filter { node ->
                    val title = resolveNodeTitle(node).lowercase()
                    val key = node.route.lowercase()
                    title.contains(query) || key.contains(query)
                }
            }
        }

    val listState = rememberLazyListState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Special Songs") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search events...") },
                    modifier = Modifier.fillMaxWidth(),
                )
                LazyColumn(
                    modifier =
                        Modifier
                            .heightIn(max = 400.dp)
                            .fillMaxWidth()
                            .verticalScrollbar(listState),
                    state = listState,
                ) {
                    items(filteredNodes) { node ->
                        val displayTitle = resolveNodeTitle(node)
                        ListItem(
                            headlineContent = {
                                Text(displayTitle)
                            },
                            modifier =
                                Modifier.clickable {
                                    val key =
                                        node.filename
                                            ?.split("/")
                                            ?.last()
                                            ?.removeSuffix(".json")
                                    if (key != null) {
                                        onEventSelected(key, displayTitle)
                                    }
                                },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
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