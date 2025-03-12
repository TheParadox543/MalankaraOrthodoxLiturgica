package com.example.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun SettingsScreen(navController: NavController, prayerViewModel: PrayerViewModel) {
    val selectedLanguage by prayerViewModel.selectedLanguage.collectAsState()
    val languages = listOf("English" to "en", "Malayalam" to "ml", "Manglish" to "mn")

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            "Select Language",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        LanguageDropdownMenu(
            options = languages,
            selectedOption = languages.firstOrNull { it.second == selectedLanguage }?.first ?: "Select",
            onOptionSelected = { prayerViewModel.setLanguage(it) }
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

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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