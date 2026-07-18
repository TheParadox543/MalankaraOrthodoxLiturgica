package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Check_circle
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.LanguageDropdownMenu
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Prose
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.Song
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.SoundModeDropdownMenu
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.OnboardingStage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onboardingViewModel: OnboardingViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onNavigateToHome: () -> Unit,
    requestDndPermission: () -> Unit = {},
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    val stage by onboardingViewModel.onboardingStage.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onboardingViewModel.refreshDndPermissionStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        onScaffoldStateChanged(ScaffoldUiState.None)
        if (onboardingViewModel.onboardingStage.value == OnboardingStage.WELCOME) {
            onboardingViewModel.logTutorialStart()
        }
    }

    AnimatedContent(
        targetState = stage,
        transitionSpec = {
            if (targetState.value > initialState.value) {
                slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith
                    slideOutHorizontally { it } + fadeOut()
            }
        },
        label = "OnboardingContent"
    ) { currentStage ->
        when (currentStage) {
            OnboardingStage.WELCOME -> WelcomePage(
                onboardingViewModel = onboardingViewModel,
                contentPadding = contentPadding,
                onContinue = { onboardingViewModel.nextPage() },
                onNavigateToHome = onNavigateToHome
            )

            OnboardingStage.SONG_WRAP -> SongWrapPage(
                onboardingViewModel = onboardingViewModel,
                contentPadding = contentPadding,
                onNext = { onboardingViewModel.nextPage() },
                onBack = { onboardingViewModel.previousPage() },
                onSkip = {
                    onboardingViewModel.skipOnboarding()
                    onNavigateToHome()
                }
            )

            OnboardingStage.SOUND_MODE -> SoundModePage(
                onboardingViewModel = onboardingViewModel,
                contentPadding = contentPadding,
                requestDndPermission = requestDndPermission,
                onNext = { onboardingViewModel.nextPage() },
                onBack = { onboardingViewModel.previousPage() },
                onSkip = {
                    onboardingViewModel.skipOnboarding()
                    onNavigateToHome()
                }
            )

            OnboardingStage.COMPLETE -> {
                FinishPage(
                    contentPadding = contentPadding,
                    onFinish = {
                        onboardingViewModel.finishOnboarding()
                        onNavigateToHome()
                    },
                    onBack = { onboardingViewModel.previousPage() }
                )
            }
        }
    }
}

@Composable
fun WelcomePage(
    onboardingViewModel: OnboardingViewModel,
    contentPadding: PaddingValues,
    onContinue: () -> Unit,
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
        modifier = Modifier
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

        Row(
            Modifier
                .padding(12.dp)
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
                modifier = Modifier.weight(1f)
            )
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
                onboardingViewModel.setFontScaleFromSettings(
                    AppFontScale.fromScale(sliderPositionFloat)
                )
            },
            modifier = Modifier.width(240.dp),
            valueRange = 0.7f..1.4f,
            steps = 3,
        )

        if (prayers.isNotEmpty()) {
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

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                onboardingViewModel.skipOnboarding()
                onNavigateToHome()
            }) {
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

@Composable
fun SongWrapPage(
    onboardingViewModel: OnboardingViewModel,
    contentPadding: PaddingValues,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    val songScrollState by onboardingViewModel.songScrollState.collectAsState()
    val sampleText = "♪ Praise the Lord with all your heart and with all your soul, for His mercy endures forever.\nAlleluia!"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingHeader(
            title = "Song Text Wrapping",
            description = "Choose how song lyrics are displayed.\n\nWrapped text allows long lines to continue onto the next line when needed.\n\nOriginal formatting preserves the manually inserted line breaks from the hymn."
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "Preview:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(8.dp))

        Text(
            "Wrapped",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Song(text = sampleText, isHorizontal = false)

        Spacer(Modifier.height(16.dp))

        Text(
            "Original",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Song(text = sampleText, isHorizontal = true)

        Spacer(Modifier.height(24.dp))

        Text(
            text = "On wider devices such as tablets or large phones, this setting may produce little or no visible difference because there is sufficient screen width. It is most useful on smaller phones.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onboardingViewModel.setSongScrollState(false) }
            ) {
                RadioButton(
                    selected = !songScrollState,
                    onClick = { onboardingViewModel.setSongScrollState(false) }
                )
                Text("Wrap song text")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onboardingViewModel.setSongScrollState(true) }
            ) {
                RadioButton(
                    selected = songScrollState,
                    onClick = { onboardingViewModel.setSongScrollState(true) }
                )
                Text("Preserve original line formatting")
            }
        }

        Spacer(Modifier.weight(1f))
        OnboardingNavigationButtons(onNext, onBack, onSkip)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundModePage(
    onboardingViewModel: OnboardingViewModel,
    contentPadding: PaddingValues,
    requestDndPermission: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    val soundMode by onboardingViewModel.soundMode.collectAsState()
    val soundRestoreDelay by onboardingViewModel.soundRestoreDelay.collectAsState()
    val hasPermission by onboardingViewModel.hasDndPermission.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OnboardingHeader(
            title = "Automatic Silent Mode",
            description = "Liturgica can automatically enable Do Not Disturb while you pray and restore your sound afterwards."
        )

        Spacer(Modifier.height(32.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Sound Mode",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            SoundModeDropdownMenu(
                selectedSoundMode = soundMode,
                onOptionSelected = { onboardingViewModel.setSoundMode(it) },
                hasPermission = hasPermission,
                modifier = Modifier.weight(1f)
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayText = if (soundRestoreDelay >= 60) {
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
                    modifier = Modifier.weight(1f)
                ) {
                    TextField(
                        value = displayText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = timeExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = timeExpanded,
                        onDismissRequest = { timeExpanded = false }
                    ) {
                        options.forEach { minutes ->
                            val label = if (minutes >= 60) "${minutes / 60} hour" else "$minutes minutes"
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    onboardingViewModel.setSoundRestoreDelay(minutes)
                                    timeExpanded = false
                                }
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

@Composable
fun FinishPage(
    contentPadding: PaddingValues,
    onFinish: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = MaterialIcons.Rounded.Check_circle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "You're all set!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Your preferences have been saved. You can change them anytime from Settings.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Praying")
        }
        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}

@Composable
fun OnboardingHeader(title: String, description: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}

@Composable
fun OnboardingNavigationButtons(
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onSkip) {
                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onNext) {
                Text("Next")
            }
        }
    }
}
