package com.paradox543.malankaraorthodoxliturgica.feature.settings.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Info
import com.composables.icons.materialicons.rounded.Share
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.FontScaleDropdownMenu
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.LanguageDropdownMenu
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.SoundModeDropdownMenu
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.OnboardingStage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.feature.settings.Res
import com.paradox543.malankaraorthodoxliturgica.feature.settings.app_share_qr
import com.paradox543.malankaraorthodoxliturgica.feature.settings.play_logo
import com.paradox543.malankaraorthodoxliturgica.feature.settings.share_icon
import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit,
    requestDndPermission: () -> Unit,
    settingsViewModel: SettingsViewModel,
    shareService: ShareService,
    showSoundModeSetting: Boolean,
    contentPadding: PaddingValues = PaddingValues(),
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()
    val selectedFontScale by settingsViewModel.fontScale.collectAsState()
    val soundMode by settingsViewModel.soundMode.collectAsState()
    val soundRestoreDelay by settingsViewModel.soundRestoreDelay.collectAsState()
    val songScrollState by settingsViewModel.songScrollState.collectAsState()
    val hasPermission by settingsViewModel.hasDndPermission.collectAsState()
    val scrollState = rememberScrollState()
    val bottomSheetState = rememberModalBottomSheetState()
    val showQrCodeDialog = rememberSaveable { mutableStateOf(false) }
    val showShareAppBottomSheet = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        settingsViewModel.shareApp.collect {
            shareService.shareAppLink(
                shareSubject = "Malankara Orthodox Liturgica",
                shareMessage =
                    "Welcome to Liturgica: A digital repository for " +
                        "all your books in the Malankara Orthodox Church",
            )
        }
    }

    LaunchedEffect(Unit) { onScaffoldStateChanged(ScaffoldUiState.Standard("Settings", showFab = false)) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(12.dp))

        // Language Selection
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Select Language",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            LanguageDropdownMenu(
                selectedOption = selectedLanguage,
                onOptionSelected = { settingsViewModel.setLanguage(it) },
            )
        }

        // Font Size Selection
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Select Font Size",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            FontScaleDropdownMenu(
                selectedFontScale = selectedFontScale,
                onOptionSelected = { settingsViewModel.setFontScaleFromSettings(it) },
            )
        }

        // Sound Mode Selection
        if (showSoundModeSetting) {
            Column(Modifier.padding(12.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Select Sound Mode",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    SoundModeDropdownMenu(
                        selectedSoundMode = soundMode,
                        onOptionSelected = { selectedSoundMode ->
                            settingsViewModel.setSoundMode(selectedSoundMode)
                        },
                        hasPermission = hasPermission,
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
                    Spacer(Modifier.height(8.dp))
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
                            "Normal restored after:",
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                        )

                        var timeExpanded by remember { mutableStateOf(false) }
                        val options = remember(settingsViewModel.debugMode) {
                            if (settingsViewModel.debugMode) listOf(1, 5, 15, 30, 60)
                            else listOf(5, 15, 30, 60)
                        }

                        ExposedDropdownMenuBox(
                            expanded = timeExpanded,
                            onExpandedChange = { timeExpanded = it },
                            modifier = Modifier.width(160.dp),
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
                                    val label = if (minutes >= 60) "${minutes / 60} hour" else "$minutes minutes"
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            settingsViewModel.setSoundRestoreDelay(minutes)
                                            timeExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Song Scroll State
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Wrap Song lines",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = !songScrollState,
                onCheckedChange = { settingsViewModel.setSongScrollState(!it) },
            )
        }

        // About the app option
        ListItem(
            modifier = Modifier.clickable { onNavigateToAbout() },
            leadingContent = {
                Icon(
                    MaterialIcons.Rounded.Info,
                    contentDescription = "About App Icon",
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            },
            headlineContent = {
                Text(
                    "About the App",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            colors =
                ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    headlineColor = MaterialTheme.colorScheme.onBackground,
                ),
        )

        // Share App
        ListItem(
            headlineContent = {
                Text(
                    "Share this App",
                    style = MaterialTheme.typography.titleSmall,
                )
            },
            modifier = Modifier.clickable { showShareAppBottomSheet.value = true },
            leadingContent = {
                Icon(
                    imageVector = MaterialIcons.Rounded.Share,
                    contentDescription = "Share App",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            },
            colors =
                ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    headlineColor = MaterialTheme.colorScheme.onBackground,
                ),
        )

        if (settingsViewModel.debugMode) {
            Spacer(Modifier.height(8.dp))
            Text("Debug Settings", style = MaterialTheme.typography.titleSmall)

            var debugExpanded by remember { mutableStateOf(false) }
            val currentStageInt by settingsViewModel.onboardingStage.collectAsState()
            val currentStage = OnboardingStage.fromInt(currentStageInt)

            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Onboarding Stage")
                ExposedDropdownMenuBox(
                    expanded = debugExpanded,
                    onExpandedChange = { debugExpanded = it },
                    modifier = Modifier.width(160.dp),
                ) {
                    TextField(
                        value = currentStage.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = debugExpanded)
                        },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = debugExpanded,
                        onDismissRequest = { debugExpanded = false },
                    ) {
                        OnboardingStage.entries.forEach { stage ->
                            DropdownMenuItem(
                                text = { Text(stage.name) },
                                onClick = {
                                    settingsViewModel.setOnboardingStage(stage.value)
                                    debugExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showQrCodeDialog.value) {
        QrCodeShareDialog(onDismissRequest = { showQrCodeDialog.value = false })
    }

    if (showShareAppBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showShareAppBottomSheet.value = false },
            sheetState = bottomSheetState,
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Card(
                    Modifier
                        .weight(0.5f)
                        .height(200.dp)
                        .padding(8.dp)
                        .clickable(
                            onClick = {
                                showShareAppBottomSheet.value = false
                                settingsViewModel.onShareAppClicked()
                            },
                        ),
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Share link",
                            Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Image(
                            painterResource(Res.drawable.share_icon),
                            "Share icon",
                            Modifier.size(60.dp),
                        )
                    }
                }
                Card(
                    Modifier
                        .weight(0.5f)
                        .height(200.dp)
                        .padding(8.dp)
                        .clickable {
                            showShareAppBottomSheet.value = false
                            showQrCodeDialog.value = true
                        },
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Generate QR code",
                        )
                        Image(
                            painterResource(Res.drawable.play_logo),
                            "Play store logo",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QrCodeShareDialog(onDismissRequest: () -> Unit) {
    // You can determine a fixed size for the QR code display here if you want
    val qrCodeDisplaySizeDp = 200.dp // Example fixed size

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Scan to Get the App!") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Load the pre-generated QR code image directly from drawables
                Image(
                    painter = painterResource(Res.drawable.app_share_qr),
                    contentDescription = "QR Code for App Store Link",
                    modifier =
                        Modifier
                            .size(qrCodeDisplaySizeDp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Scan this QR code with any QR scanner app to download from the Play Store.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        },
    )
}
