package com.paradox543.malankaraorthodoxliturgica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.core.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    settingsViewModel: SettingsViewModel,
    prayerViewModel: PrayerViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onNavigateToHome: () -> Unit,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    var selectedLanguage by remember { mutableStateOf(AppLanguage.MALAYALAM) }
    val selectedFontScale by settingsViewModel.fontScale.collectAsState()

    var languageExpanded by remember { mutableStateOf(false) }
    val prayers by prayerViewModel.prayers.collectAsState()
    val filename = "commonPrayers/lords.json"

    LaunchedEffect(Unit) { onScaffoldStateChanged(ScaffoldUiState.None) }

    LaunchedEffect(selectedLanguage) {
        prayerViewModel.loadPrayerElements(filename, selectedLanguage)
    }
    LaunchedEffect(Unit) {
        settingsViewModel.logTutorialStart()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Welcome to Liturgica!",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
        )
        Text(
            text = "Please choose your preferred language and font size.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp),
        )

        // --- Language Selection ---
        ExposedDropdownMenuBox(
            expanded = languageExpanded,
            onExpandedChange = { languageExpanded = it },
        ) {
            OutlinedTextField(
                value = selectedLanguage.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Language") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded)
                },
                modifier =
                    Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = languageExpanded,
                onDismissRequest = { languageExpanded = false },
                modifier = Modifier.fillMaxWidth(),
            ) {
                AppLanguage.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(it.displayName) },
                        onClick = {
                            selectedLanguage = it
                            languageExpanded = false
                        },
                        enabled = it != selectedLanguage,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // --- Font Size Selection ---
        Text(
            text = "Font Size: ${selectedFontScale.displayName}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = selectedFontScale.scaleFactor,
            onValueChange = { sliderPositionFloat ->
                settingsViewModel.setFontScaleFromSettings(AppFontScale.fromScale(sliderPositionFloat))
            },
            modifier = Modifier.width(240.dp),
            valueRange = 0.7f..1.4f,
            steps = 3,
        )

        if (!prayers.isEmpty()) {
            Column(
                modifier = Modifier.padding(vertical = 28.dp),
            ) {
                Text(
                    "Sample Prayer",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                (prayers[1] as? PrayerElement.Prose)?.let { Prose(it.content) }
            }
        }

        // --- Get Started Button ---
        Button(
            onClick = {
                settingsViewModel.setLanguage(selectedLanguage)
                settingsViewModel.setFontScaleFromSettings(selectedFontScale)
                settingsViewModel.setOnboardingCompleted()
                onNavigateToHome()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Get Started!")
        }
        Text("Version: ${BuildConfig.VERSION_NAME}")
    }
}
