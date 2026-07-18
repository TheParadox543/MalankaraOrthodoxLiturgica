package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Check_circle
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.OnboardingStage
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
        val observer =
            LifecycleEventObserver { _, event ->
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
        label = "OnboardingContent",
    ) { currentStage ->
        when (currentStage) {
            OnboardingStage.WELCOME -> {
                WelcomePage(
                    onboardingViewModel = onboardingViewModel,
                    contentPadding = contentPadding,
                    onContinue = { onboardingViewModel.nextPage() },
                    onNavigateToHome = onNavigateToHome,
                )
            }

            OnboardingStage.SONG_WRAP -> {
                SongWrapPage(
                    onboardingViewModel = onboardingViewModel,
                    contentPadding = contentPadding,
                    onNext = { onboardingViewModel.nextPage() },
                    onBack = { onboardingViewModel.previousPage() },
                    onSkip = {
                        onboardingViewModel.skipOnboarding()
                        onNavigateToHome()
                    },
                )
            }

            OnboardingStage.SOUND_MODE -> {
                SoundModePage(
                    onboardingViewModel = onboardingViewModel,
                    contentPadding = contentPadding,
                    requestDndPermission = requestDndPermission,
                    onNext = { onboardingViewModel.nextPage() },
                    onBack = { onboardingViewModel.previousPage() },
                    onSkip = {
                        onboardingViewModel.skipOnboarding()
                        onNavigateToHome()
                    },
                )
            }

            OnboardingStage.COMPLETE -> {
                FinishPage(
                    contentPadding = contentPadding,
                    onFinish = {
                        onboardingViewModel.finishOnboarding()
                        onNavigateToHome()
                    },
                    onBack = { onboardingViewModel.previousPage() },
                )
            }
        }
    }
}

@Composable
fun FinishPage(
    contentPadding: PaddingValues,
    onFinish: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.weight(1f))
        Icon(
            imageVector = MaterialIcons.Rounded.Check_circle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "You're all set!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Your preferences have been saved. You can change them anytime from Settings.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Start Praying")
        }
        TextButton(onClick = onBack) {
            Text("Back")
        }
    }
}

@Composable
fun OnboardingHeader(
    title: String,
    description: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun OnboardingNavigationButtons(
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
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
