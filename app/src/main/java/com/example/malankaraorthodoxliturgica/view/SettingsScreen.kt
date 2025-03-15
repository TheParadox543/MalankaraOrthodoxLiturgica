package com.example.malankaraorthodoxliturgica.view

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import kotlinx.coroutines.selects.select

@Composable
fun SettingsScreen(navController: NavController, prayerViewModel: PrayerViewModel) {
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()

    val languages = listOf("English" to "en", "Malayalam" to "ml", "Manglish" to "mn")
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
