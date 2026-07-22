package com.paradox543.malankaraorthodoxliturgica.core.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdownMenu(
    selectedOption: AppLanguage,
    onOptionSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedOption.displayName.split(" ")[0],
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .width(160.dp),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            AppLanguage.entries.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.displayName) },
                    onClick = {
                        onOptionSelected(language)
                        expanded = false
                    },
                    enabled = language != selectedOption,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontScaleDropdownMenu(
    selectedFontScale: AppFontScale,
    onOptionSelected: (AppFontScale) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedFontScale.displayName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .width(160.dp),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            AppFontScale.entries.forEach { appFontSize ->
                DropdownMenuItem(
                    text = { Text(appFontSize.displayName) },
                    onClick = {
                        onOptionSelected(appFontSize)
                        expanded = false
                    },
                    enabled = appFontSize != selectedFontScale,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundModeDropdownMenu(
    selectedSoundMode: SoundMode,
    onOptionSelected: (SoundMode) -> Unit,
    hasPermission: Boolean,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (hasPermission) expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedSoundMode.name,
            onValueChange = {},
            enabled = hasPermission,
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier =
                Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .width(160.dp),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            SoundMode.entries.forEach { soundMode ->
                DropdownMenuItem(
                    text = { Text(soundMode.name) },
                    onClick = {
                        onOptionSelected(soundMode)
                        expanded = false
                    },
                    enabled = soundMode != selectedSoundMode,
                )
            }
        }
    }
}
