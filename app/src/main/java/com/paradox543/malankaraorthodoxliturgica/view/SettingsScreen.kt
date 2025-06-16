package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.data.model.AppFontSize
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val selectedLanguage by settingsViewModel.selectedLanguage.collectAsState()
    val selectedFontSize by settingsViewModel.selectedFontSize.collectAsState()
    val songScrollState by settingsViewModel.songScrollState.collectAsState()
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopNavBar("Settings", navController) },
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Select Language",
                        fontSize = selectedFontSize.fontSize,
                        fontWeight = FontWeight.Bold,
                    )
                    LanguageDropdownMenu(
                        selectedOption = selectedLanguage,
                        selectedFontSize = selectedFontSize.fontSize,
                        onOptionSelected = { settingsViewModel.setLanguage(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Font Size Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Select Font Size",
                        fontSize = selectedFontSize.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    FontSizeDropdownMenu(
                        selectedFontSize = selectedFontSize,
                        onOptionSelected = { settingsViewModel.setFontSizeFromSettings(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Song Scroll State
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    Modifier.padding(8.dp)
                ) {
                    Row(
                        Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Text Layout for Songs",
                            fontSize = selectedFontSize.fontSize,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                        )
                        Switch(
                            checked = songScrollState,
                            onCheckedChange = { settingsViewModel.setSongScrollState(it) }
                        )
                    }
                    if (songScrollState) {
                        Text(
                            "Long lines will extend off-screen and can be scrolled horizontally",
                            fontSize = selectedFontSize.fontSize
                        )
                    } else {
                        Text(
                            "Lines stay within the screen",
                            fontSize = selectedFontSize.fontSize,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ListItem(
                modifier = Modifier.clickable { showDialog = true },
                leadingContent = {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "About App Icon",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                },
                headlineContent = { Text("About the App", fontSize = selectedFontSize.fontSize) }
            )

            if (BuildConfig.DEBUG) {
                Spacer(Modifier.padding(16.dp))

                ElevatedButton(
                    onClick = { settingsViewModel.setOnboardingCompleted(false) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Reset onboarding")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = "About App Icon",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("About the App", fontSize = selectedFontSize.fontSize * 1.2f)
                }
            },
            text = {
                AboutAppDialogContent()
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Close", fontSize = selectedFontSize.fontSize)
                }
            }
        )
    }
}

@Composable
fun LanguageDropdownMenu(
    selectedOption: AppLanguage,
    selectedFontSize: TextUnit,
    onOptionSelected: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true }
        ) {
            Text(
                selectedOption.displayName,
                textAlign = TextAlign.Center,
                fontSize = selectedFontSize,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppLanguage.entries.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.displayName) },
                    onClick = {
                        onOptionSelected(language)
                        expanded = false
                    },
                    enabled = language != selectedOption
                )
            }
        }
    }
}

@Composable
fun FontSizeDropdownMenu(
    selectedFontSize: AppFontSize,
    onOptionSelected: (AppFontSize) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedFontSize) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.onTertiary,
            ),
        ) {
            Text(selectedText.displayName, fontSize = selectedFontSize.fontSize)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AppFontSize.entries.forEach { appFontSize ->
                DropdownMenuItem(
                    text = { Text(appFontSize.displayName) },
                    onClick = {
                        selectedText = appFontSize
                        onOptionSelected(appFontSize)
                        expanded = false
                    },
                    enabled = appFontSize != selectedFontSize
                )
            }
        }
    }
}

@Composable
fun AboutAppDialogContent(selectedFontSize: TextUnit = 16.sp) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            "This app is designed to provide prayers and religious content for the Malankara Orthodox Syrian Church.",
            fontSize = selectedFontSize
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "📜 Credits & Contributors",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = selectedFontSize * 1.2f
        )
        Text(
            "- Samuel Alex Koshy – Development, Implementation, UI Design, and Text Translations",
            fontSize = selectedFontSize
        )
        Text(
            "- Shriganesh Keshrimal Purohit – Guidance, Structural Planning, and Development Insights",
            fontSize = selectedFontSize
        )
        Text(
            "- Shaun John, Lisa Shibu George, Sabu John, Saira Susan Koshy, Sunitha Mathew & " +
                    "Nohan George– Additional Text Translations and Preparation",
            fontSize = selectedFontSize
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Version: ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            fontSize = selectedFontSize * 0.8f
        )
    }
}