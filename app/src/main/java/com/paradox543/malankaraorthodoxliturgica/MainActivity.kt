package com.paradox543.malankaraorthodoxliturgica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.core.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.StartupState
import com.paradox543.malankaraorthodoxliturgica.ui.navigation.NavGraph
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Thin Android entry point. Responsible for:
 * - Splash screen management
 * - In-app update/review lifecycle hooks
 * - Sound mode management
 * - Theme setup
 *
 * All navigation and UI logic lives in [NavGraph].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var inAppUpdateManager: InAppUpdateManager

    @Inject lateinit var inAppReviewManager: InAppReviewManager

    @Inject lateinit var analyticsService: AnalyticsService

    @Inject lateinit var shareService: ShareService

    @Inject lateinit var soundModeManager: SoundModeManager

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val startupViewModel: StartupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        inAppUpdateManager.checkForUpdate(this)

        var keepSplashOn by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { keepSplashOn }

        lifecycleScope.launch {
            startupViewModel.startupState.collect { state ->
                if (state is StartupState.Ready) keepSplashOn = false
            }
        }

        setContent {
            val startupState by startupViewModel.startupState.collectAsState()
            when (val s = startupState) {
                is StartupState.Loading -> {}

                is StartupState.Ready -> {
                    val soundMode by settingsViewModel.soundMode.collectAsState()
                    val textScale by settingsViewModel.fontScale.collectAsState()
                    val language by settingsViewModel.selectedLanguage.collectAsState()

                    // Android system concern: apply the user's preferred sound/DnD mode
                    LaunchedEffect(soundMode) {
                        soundModeManager.apply(soundMode)
                    }

                    MalankaraOrthodoxLiturgicaTheme(
                        language = language,
                        textScale = textScale,
                    ) {
                        NavGraph(
                            onboardingCompleted = s.onboardingCompleted,
                            inAppUpdateManager = inAppUpdateManager,
                            inAppReviewManager = inAppReviewManager,
                            analyticsService = analyticsService,
                            shareService = shareService,
                            settingsViewModel = settingsViewModel,
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdateManager.resumeUpdate()
        settingsViewModel.refreshDndPermissionStatus()
        soundModeManager.cancelRestoreWork()
        val soundMode = settingsViewModel.soundMode.value
        soundModeManager.apply(soundMode)
    }

    override fun onPause() {
        super.onPause()
        inAppUpdateManager.unregisterListener()
        soundModeManager.scheduleRestore(settingsViewModel.soundRestoreDelay.value)
    }
}
