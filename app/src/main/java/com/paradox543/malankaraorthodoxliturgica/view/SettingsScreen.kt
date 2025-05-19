package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun SettingsScreen(navController: NavController, prayerViewModel: PrayerViewModel, navViewModel: NavViewModel) {
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }

    val languages = listOf(
        "മലയാളം" to "ml",
//        "English" to "en",
        "Manglish" to "mn"
    )
    val fontSizes = listOf(
        "Very Small" to 12.sp,
        "Small" to 14.sp,
        "Medium" to 16.sp,
        "Large" to 20.sp,
        "Very Large" to 24.sp
    )

    Scaffold(
        topBar = {
           TopNavBar(
               navController = navController,
               prayerViewModel = prayerViewModel,
               navViewModel = navViewModel
           )
        },
        bottomBar = {BottomNavBar(navController)}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = "Select Language",
                    fontSize = selectedFontSize,
                    fontWeight = FontWeight.Bold
                )
                LanguageDropdownMenu(
                    options = languages,
                    selectedOption = languages.firstOrNull { it.second == selectedLanguage }?.first
                        ?: "Select",
                    selectedFontSize = selectedFontSize,
                    onOptionSelected = { prayerViewModel.setLanguage(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Font Size Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = "Select Font Size",
                    fontSize = selectedFontSize,
                    fontWeight = FontWeight.Bold
                )
                FontSizeDropdownMenu(
                    options = fontSizes,
                    selectedOption = fontSizes.firstOrNull { it.second == selectedFontSize }?.first
                        ?: "Medium",
                    selectedFontSize = selectedFontSize,
                    onOptionSelected = { prayerViewModel.setFontSize(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ListItem(
                modifier = Modifier.clickable { showDialog = true },
                leadingContent = {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "About App Icon",
                        tint = MaterialTheme.colorScheme.primary // Optional: Customize the icon color
                    )
                },
                headlineContent = { Text("About the App", fontSize = selectedFontSize) }
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "About App Icon",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("About the App", fontSize = selectedFontSize * 1.2f)
                }
            },
            text = {
                AboutAppDialogContent()
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Close", fontSize = selectedFontSize)
                }
            }
        )
    }
}

@Composable
fun LanguageDropdownMenu(
    options: List<Pair<String, String>>,
    selectedOption: String,
    selectedFontSize: TextUnit,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedOption, fontSize = selectedFontSize)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (optionDisplay, option) ->
                DropdownMenuItem(
                    text = { Text(optionDisplay) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FontSizeDropdownMenu(
    options: List<Pair<String, TextUnit>>,
    selectedOption: String,
    selectedFontSize: TextUnit,
    onOptionSelected: (TextUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedOption) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedText, fontSize = selectedFontSize)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (label, size) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        selectedText = label
                        onOptionSelected(size)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AboutAppDialogContent(selectedFontSize: TextUnit = 16.sp) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            "This app is designed to provide prayers and religious content for the Malankara Orthodox Syrian Church.",
            fontSize = selectedFontSize
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "📜 Credits & Contributors",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = selectedFontSize * 1.2f
        )
        Text(
            "- Samuel Alex Koshy – Development, Implementation, UI Design, and Text Translations",
            fontSize = selectedFontSize
        )
        Text(
            "- Shriganesh Keshrimal Purohit – Guidance, Structural Planning, and Development Insights",
            fontSize = selectedFontSize
        )
        Text(
            "- Shaun John, Lisa Shibu George, Sabu John, Saira Susan Koshy & Sunitha Mathew – Additional Text Translations and Preparation",
            fontSize = selectedFontSize
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Version: 0.2.3",
            style = MaterialTheme.typography.bodySmall,
            fontSize = selectedFontSize * 0.8f
        )
    }
}