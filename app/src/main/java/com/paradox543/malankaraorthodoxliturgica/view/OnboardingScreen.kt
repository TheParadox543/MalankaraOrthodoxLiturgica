package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class) // For ExposedDropdownMenuBox
@Composable
fun OnboardingScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    // Current selections (will be saved when "Get Started" is clicked)
    var selectedLanguage by remember { mutableStateOf(AppLanguage.MALAYALAM) }
    var selectedFontSize by remember { mutableStateOf(16.sp) } // Default medium size, adjust as needed

    // State for language dropdown menu
    var languageExpanded by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {
            Text(
                text = "Welcome to Liturgica!",
                fontSize = selectedFontSize * 7 / 4,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Please choose your preferred language and font size.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // --- Language Selection ---
            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedLanguage.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Language") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppLanguage.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.displayName) },
                            onClick = {
                                selectedLanguage = it
                                languageExpanded = false
                            },
                            enabled = it != selectedLanguage
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Font Size Selection ---
            Text(
                text = "Font Size: ${
                    when (selectedFontSize) {
                        12.sp -> "Small"
                        16.sp -> "Medium"
                        20.sp -> "Large"
                        else -> "Custom" // Or handle your specific sizes
                    }
                }",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = selectedFontSize.value,
                onValueChange = { selectedFontSize = it.sp },
                valueRange = 12f..20f,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // You can use RadioButtons, Slider, or simple Buttons for font size
                Button(
                    onClick = { selectedFontSize = 12.sp },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedFontSize == 12.sp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("Small")
                }
                Button(
                    onClick = { selectedFontSize = 16.sp },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedFontSize == 16.sp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("Medium")
                }
                Button(
                    onClick = { selectedFontSize = 20.sp },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedFontSize == 20.sp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("Large")
                }
            }

            Spacer(Modifier.height(48.dp))

            // --- Get Started Button ---
            Button(
                onClick = {
                    settingsViewModel.setLanguage(selectedLanguage)
                    settingsViewModel.setFontSize(selectedFontSize)
                    settingsViewModel.setOnboardingCompleted()
                    // Navigate to the home screen
                    navController.navigate("home") { // Define your main app route
                        popUpTo("onboarding_route") { // Remove onboarding from back stack
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Started!")
            }
        }
    }
}