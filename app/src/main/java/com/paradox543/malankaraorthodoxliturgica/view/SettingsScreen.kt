package com.paradox543.malankaraorthodoxliturgica.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.data.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.RestoreTimePicker
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.requestDndPermission

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
) {
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()
    val selectedFontScale by settingsViewModel.fontScale.collectAsState()
    val soundMode by settingsViewModel.soundMode.collectAsState()
    val soundRestoreDelay by settingsViewModel.soundRestoreDelay.collectAsState()
    val songScrollState by settingsViewModel.songScrollState.collectAsState()
    val hasPermission by settingsViewModel.hasDndPermission.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val bottomSheetState = rememberModalBottomSheetState()
    var showQrCodeDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var showShareAppBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopNavBar("Settings", navController) },
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.Center,
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
            Column(Modifier.padding(12.dp)) {
                Row(
                    Modifier
//                        .padding(12.dp)
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
                            onClick = { requestDndPermission(context) },
                            modifier = Modifier.padding(top = 4.dp),
                        ) {
                            Text("Grant Permission")
                        }
                    }
                } else if (soundMode != SoundMode.OFF) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val displayText =
                            if (soundRestoreDelay >= 60) {
                                "${soundRestoreDelay / 60} hr ${soundRestoreDelay % 60} min"
                            } else {
                                "$soundRestoreDelay min"
                            }
                        Text(
                            "Normal restored after:",
                            Modifier.padding(horizontal = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Card(
                            Modifier
                                .requiredWidthIn(min = 120.dp)
                                .fillMaxHeight()
                                .clickable(onClick = { showRestoreDialog = true }),
                        ) {
                            Text(displayText, Modifier.padding(4.dp))
                        }
                        if (showRestoreDialog) {
                            RestoreTimePicker(
                                onDismiss = { showRestoreDialog = false },
                                onConfirm = { minute ->
                                    Log.d("SettingsScreen", "Restore time after $minute")
                                    settingsViewModel.setSoundRestoreDelay(minute)
                                    showRestoreDialog = false
                                },
                                delayTime = soundRestoreDelay,
                            )
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
                    text = "Text Layout for Songs",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = songScrollState,
                    onCheckedChange = { settingsViewModel.setSongScrollState(it) },
                )
            }

            // About the app option
            ListItem(
                modifier = Modifier.clickable { navController.navigate(Screen.About.route) },
                leadingContent = {
                    Icon(
                        Icons.Filled.Info,
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
                modifier = Modifier.clickable { showShareAppBottomSheet = true },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Share,
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

            if (BuildConfig.DEBUG) {
                ElevatedButton(
                    onClick = { settingsViewModel.setOnboardingCompleted(false) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Text("Reset onboarding")
                }
            }
        }
    }

    if (showQrCodeDialog) {
        QrCodeShareDialog(onDismissRequest = { showQrCodeDialog = false })
    }

    if (showShareAppBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showShareAppBottomSheet = false
            },
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
                                settingsViewModel.shareAppPlayStoreLink(
                                    context = context,
                                    shareMessage =
                                        "Welcome to Liturgica: A digital repository for " +
                                            "all your books in the Malankara Orthodox Church", // Your custom message
                                )
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
                            painterResource(R.drawable.share_icon),
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
                            showQrCodeDialog = true
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
                            painterResource(R.drawable.play_logo),
                            "Play store logo",
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdownMenu(
    selectedOption: AppLanguage,
    onOptionSelected: (AppLanguage) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
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
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
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
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
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
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
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
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (hasPermission) expanded = !expanded },
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
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
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
                    painter = painterResource(id = R.drawable.app_share_qr), // Your QR image filename
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
