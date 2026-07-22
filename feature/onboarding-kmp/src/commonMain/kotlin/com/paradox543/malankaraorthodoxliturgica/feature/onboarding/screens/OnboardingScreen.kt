package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    val columnPadding = 16.dp

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
                    columnPadding = columnPadding,
                    onContinue = { onboardingViewModel.nextPage() },
                ) {
                    onboardingViewModel.skipOnboarding()
                    onNavigateToHome()
                }
            }

            OnboardingStage.SONG_WRAP -> {
                SongWrapPage(
                    onboardingViewModel = onboardingViewModel,
                    contentPadding = contentPadding,
                    columnPadding = columnPadding,
                    onNext = { onboardingViewModel.nextPage() },
                    onBack = { onboardingViewModel.previousPage() },
                ) {
                    onboardingViewModel.skipOnboarding()
                    onNavigateToHome()
                }
            }

            OnboardingStage.SOUND_MODE -> {
                SoundModePage(
                    onboardingViewModel = onboardingViewModel,
                    contentPadding = contentPadding,
                    columnPadding = columnPadding,
                    requestDndPermission = requestDndPermission,
                    onNext = { onboardingViewModel.nextPage() },
                    onBack = { onboardingViewModel.previousPage() },
                ) {
                    onboardingViewModel.skipOnboarding()
                    onNavigateToHome()
                }
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
            
            else -> {}
        }
    }
}
