package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.LanguageDropdownMenu
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel

@Composable
fun WelcomePage(
    onboardingViewModel: OnboardingViewModel,
    contentPadding: PaddingValues,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val selectedLanguage by onboardingViewModel.selectedLanguage.collectAsState()
    val selectedFontScale by onboardingViewModel.fontScale.collectAsState()
    val prayers by onboardingViewModel.prayers.collectAsState()
    val filename = "commonPrayers/lords.json"

    LaunchedEffect(selectedLanguage) {
        onboardingViewModel.loadPrayerElements(filename, selectedLanguage)
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(16.dp),
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

        Row(
            Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Language",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            LanguageDropdownMenu(
                selectedOption = selectedLanguage,
                onOptionSelected = { onboardingViewModel.setLanguage(it) },
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(12.dp))

        // --- Font Size Selection ---
        Text(
            text = "Font Size: ${selectedFontScale.displayName}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Slider(
            value = selectedFontScale.scaleFactor,
            onValueChange = { sliderPositionFloat ->
                onboardingViewModel.setFontScaleFromSettings(
                    AppFontScale.fromScale(sliderPositionFloat),
                )
            },
            modifier = Modifier.width(240.dp),
            valueRange = 0.7f..1.4f,
            steps = 3,
        )

        if (prayers.isNotEmpty()) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    "Sample Prayer",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                (prayers[1] as? PrayerElement.Prose)?.let { Prose(it.content) }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onSkip) {
                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.weight(1f),
            ) {
                Text("Continue")
            }
        }
    }
}