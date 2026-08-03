package com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel

@Composable
fun IndexScreen(
    prayerViewModel: PrayerViewModel,
    prayerNavViewModel: PrayerNavViewModel,
    contentPadding: PaddingValues,
    onPrayerNavigate: (String) -> Unit,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
) {
    val translations by prayerViewModel.translations.collectAsState()
    val indexItems by prayerNavViewModel.prayerIndex.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val filteredItems =
        remember(indexItems, searchQuery, translations) {
            indexItems
                .map { item ->
                    val title =
                        item.prayerId.split("_").joinToString(" ") { segment ->
                            translations[segment] ?: segment
                        }
                    val pathText =
                        item.path.drop(1).dropLast(1).joinToString(" > ") { segment ->
                            segment.split("_").joinToString(" ") { word ->
                                translations[word] ?: word
                            }
                        }
                    item to (title to pathText)
                }.filter { (_, display) ->
                    val (title, _) = display
                    title.contains(searchQuery, ignoreCase = true)
                }.sortedBy { (_, display) -> display.first }
        }

    LaunchedEffect(translations) {
        val title = translations["indexOfPrayers"] ?: "Index of prayers"
        onScaffoldStateChanged(ScaffoldUiState.Standard(title))
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            placeholder = { Text(translations["search"] ?: "Search") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions =
                KeyboardActions(
                    onSearch = {
                        prayerViewModel.logSearch(
                            query = searchQuery,
                            itemId = "(item not found)",
                            itemName = "(item not found)",
                        )
                        focusManager.clearFocus()
                    },
                ),
            colors =
                TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            shape = MaterialTheme.shapes.medium,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(filteredItems) { (item, display) ->
                val (title, pathText) = display
                ListItem(
                    headlineContent = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    supportingContent = {
                        if (pathText.isNotEmpty()) {
                            Text(
                                text = pathText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                    modifier =
                        Modifier.clickable {
                            prayerViewModel.logSearch(
                                query = searchQuery,
                                itemId = item.prayerId,
                                itemName = title,
                            )
                            onPrayerNavigate(item.prayerId)
                        },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}
