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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.SoundModeDropdownMenu
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.OnboardingHeader
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.components.OnboardingNavigationButtons
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundModePage(
    onboardingViewModel: OnboardingViewModel,
    contentPadding: PaddingValues,
    columnPadding: Dp,
    requestDndPermission: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    val soundMode by onboardingViewModel.soundMode.collectAsState()
    val soundRestoreDelay by onboardingViewModel.soundRestoreDelay.collectAsState()
    val hasPermission by onboardingViewModel.hasDndPermission.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(columnPadding)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OnboardingHeader(
            title = "Automatic Silent Mode",
            description = "Liturgica can automatically enable Do Not Disturb while you pray and restore your sound afterwards.",
        )

        Spacer(Modifier.height(32.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Sound Mode",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            SoundModeDropdownMenu(
                selectedSoundMode = soundMode,
                onOptionSelected = { onboardingViewModel.setSoundMode(it) },
                hasPermission = hasPermission,
                modifier = Modifier.weight(1f),
            )
        }

        if (!hasPermission) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "This feature requires DND permission.",
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
                Button(
                    onClick = { requestDndPermission() },
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text("Grant Permission")
                }
            }
        } else if (soundMode != SoundMode.OFF) {
            Spacer(Modifier.height(24.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val displayText =
                    if (soundRestoreDelay >= 60) {
                        "${soundRestoreDelay / 60} hour"
                    } else {
                        "$soundRestoreDelay minutes"
                    }
                Text(
                    "Auto-restore",
                    Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                )

                var timeExpanded by remember { mutableStateOf(false) }
                val options = listOf(5, 15, 30, 60)

                ExposedDropdownMenuBox(
                    expanded = timeExpanded,
                    onExpandedChange = { timeExpanded = it },
                    modifier = Modifier.weight(1f),
                ) {
                    TextField(
                        value = displayText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded)
                        },
                        modifier =
                            Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = timeExpanded,
                        onDismissRequest = { timeExpanded = false },
                    ) {
                        options.forEach { minutes ->
                            val label =
                                if (minutes >= 60) "${minutes / 60} hour" else "$minutes minutes"
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    onboardingViewModel.setSoundRestoreDelay(minutes)
                                    timeExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        OnboardingNavigationButtons(onNext, onBack, onSkip)
    }
}