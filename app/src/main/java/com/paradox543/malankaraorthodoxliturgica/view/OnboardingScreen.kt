package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.data.model.AppFontSize
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class) // For ExposedDropdownMenuBox
@Composable
fun OnboardingScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    prayerViewModel: PrayerViewModel,
) {
    // Current selections (will be saved when "Get Started" is clicked)
    var selectedLanguage by remember { mutableStateOf(AppLanguage.MALAYALAM) }
    var selectedFontSize by remember { mutableStateOf(AppFontSize.Medium) } // Default medium size, adjust as needed

    // State for language dropdown menu
    var languageExpanded by remember { mutableStateOf(false) }
    val prayers by prayerViewModel.prayers.collectAsState()
    val filename = "commonprayers/lords.json"
    LaunchedEffect(selectedLanguage) {
        prayerViewModel.loadPrayerElements(filename, selectedLanguage)
    }
    LaunchedEffect(Unit) {
        settingsViewModel.logTutorialStart()
    }

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
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
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
                text = "Font Size: ${selectedFontSize.displayName}",
                fontSize = selectedFontSize.fontSize
            )
            Slider(
                value = selectedFontSize.intValue.toFloat(),
                onValueChange = { sliderPositionFloat ->
                    selectedFontSize = AppFontSize.fromInt(sliderPositionFloat.toInt())
                },
                modifier = Modifier.width(240.dp),
                valueRange = 8f..24f,
                steps = 3
            )

            Spacer(Modifier.height(20.dp))

            if (!prayers.isEmpty()) {
                Column(
                    modifier = Modifier.height(300.dp)
                ) {
                    Text(
                        "Sample Prayer",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    PrayerElementRenderer(prayers[1], selectedFontSize, prayerViewModel, filename)
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- Get Started Button ---
            Button(
                onClick = {
                    settingsViewModel.setLanguage(selectedLanguage)
                    settingsViewModel.setFontSizeFromSettings(selectedFontSize)
                    settingsViewModel.setOnboardingCompleted()
                    // Navigate to the home screen
                    navController.navigate("home") { // Define your main app route
                        popUpTo("onboarding") { // Remove onboarding from back stack
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Started!")
            }
            Text("Version: ${BuildConfig.VERSION_NAME}")
        }
    }
}