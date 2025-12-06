package com.paradox543.malankaraorthodoxliturgica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.paradox543.malankaraorthodoxliturgica.domain.model.StartupState
import com.paradox543.malankaraorthodoxliturgica.navigation.NavGraph
import com.paradox543.malankaraorthodoxliturgica.services.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.services.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.services.sound.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SettingsViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Inject the update manager for handling in-app updates.
    @Inject
    lateinit var inAppUpdateManager: InAppUpdateManager

    @Inject
    lateinit var inAppReviewManager: InAppReviewManager

    @Inject
    lateinit var soundModeManager: SoundModeManager

    // Initialize ViewModels needed for startup logic.
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val startupViewModel: StartupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Check for app updates as soon as the app starts.
        inAppUpdateManager.checkForUpdate(this)

        // Keep the splash screen active until the initial data is loaded.
        var keepSplashOn by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { keepSplashOn }

        // Launch a coroutine to load necessary data before hiding the splash screen.
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
                    val onboardingCompleted = s.onboardingCompleted
                    val soundMode by settingsViewModel.soundMode.collectAsState()
                    MalankaraOrthodoxLiturgicaTheme(
                        language = s.language,
                        textScale = s.fontScale,
                    ) {
                        val snackbarHostState = remember { SnackbarHostState() }

                        // 2. Collect the state from the manager.
                        val updateDownloaded by inAppUpdateManager.updateDownloaded.collectAsState()

                        // 3. Use LaunchedEffect to react to the state change.
                        LaunchedEffect(updateDownloaded) {
                            if (updateDownloaded) {
                                val result =
                                    snackbarHostState.showSnackbar(
                                        message = "An update has just been downloaded.",
                                        actionLabel = "RESTART",
                                        duration = SnackbarDuration.Indefinite, // Stays until dismissed or actioned
                                    )
                                // 4. Perform action based on user interaction.
                                if (result == SnackbarResult.ActionPerformed) {
                                    inAppUpdateManager.completeUpdate()
                                }
                            }
                        }
                        LaunchedEffect(soundMode) {
                            soundModeManager.apply(soundMode)
                        }
                        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
                            NavGraph(inAppReviewManager, Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // The selected code from the Canvas is used here.
        // It checks if an update was already downloaded while the app was in the background.
        inAppUpdateManager.resumeUpdate()

        settingsViewModel.refreshDndPermissionStatus()
        soundModeManager.cancelRestoreWork()
        val soundMode = settingsViewModel.soundMode.value
        soundModeManager.apply(soundMode)
    }

    override fun onPause() {
        super.onPause()
        inAppUpdateManager.unregisterListener()
        // Schedule sound restoration when app goes to background
        soundModeManager.scheduleRestore(settingsViewModel.soundRestoreDelay.value)
    }
}
