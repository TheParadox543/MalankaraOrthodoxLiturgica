package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun SettingsScreen(navController: NavController, prayerViewModel: PrayerViewModel) {
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()

    val languages = listOf(
        "Malayalam" to "ml",
        "English" to "en",
        "Manglish" to "mn"
    )
    val fontSizes = listOf(
        "Very Small" to 12.sp,
        "Small" to 14.sp,
        "Medium" to 16.sp,
        "Large" to 20.sp,
        "Very Large" to 24.sp
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ){
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ){
            Text(
                text = "Select Language",
                fontSize = selectedFontSize,
                fontWeight = FontWeight.Bold
            )
            LanguageDropdownMenu(
                options = languages,
                selectedOption = languages.firstOrNull { it.second == selectedLanguage }?.first
                    ?: "Select",
                onOptionSelected = { prayerViewModel.setLanguage(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Font Size Selection
        Card (
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
                onOptionSelected = { prayerViewModel.setFontSize(it) }
            )
        }
        ListItem(
            modifier = Modifier.clickable { navController.navigate("aboutApp") },
            headlineContent = { Text("About the App") }
        )
    }
}

@Composable
fun LanguageDropdownMenu(
    options: List<Pair<String, String>>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedOption)
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
    onOptionSelected: (TextUnit) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedOption) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedText)
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
fun AboutAppScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)) // Semi-transparent overlay
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(8.dp)
                ) // Opaque background
                .padding(16.dp)
        ) {
            Text("About the App", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text("This app is designed to provide prayers and religious content for the Malankara Orthodox Syrian Church.")
            Spacer(Modifier.height(16.dp))
            Text("📜 Credits & Contributors", style = MaterialTheme.typography.headlineMedium)
            Text("- Samuel Alex Koshy – Development, Implementation, UI Design, and Text Translations")
            Text("- Shriganesh Keshrimal Purohit – Guidance, Structural Planning, and Development Insights")
            Text("- Shaun John, Lisa Shibu George & Sabu John – Additional Text Translations and Preparation")
            Spacer(Modifier.height(16.dp))
            Text("Version: 0.2.0", style = MaterialTheme.typography.bodySmall)
        }
    }
}
