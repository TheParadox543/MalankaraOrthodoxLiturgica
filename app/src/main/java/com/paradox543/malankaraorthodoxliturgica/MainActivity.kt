package com.paradox543.malankaraorthodoxliturgica

import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.paradox543.malankaraorthodoxliturgica.data.repository.CalendarRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repository.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.data.repository.RestoreSoundWorker
import com.paradox543.malankaraorthodoxliturgica.data.repository.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.domain.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.navigation.NavGraph
import com.paradox543.malankaraorthodoxliturgica.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Inject the update manager for handling in-app updates.
    @Inject
    lateinit var inAppUpdateManager: InAppUpdateManager

    @Inject
    lateinit var calendarRepository: CalendarRepository

    @Inject
    lateinit var workManager: WorkManager

    private var previousInterruptionFilter: Boolean? = null

    // Initialize ViewModels needed for startup logic.
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Check for app updates as soon as the app starts.
        if (!BuildConfig.DEBUG) {
            inAppUpdateManager.checkForUpdate(this)
        }

        // Keep the splash screen active until the initial data is loaded.
        var isInitialDataLoaded by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isInitialDataLoaded }

        // Launch a coroutine to load necessary data before hiding the splash screen.
        lifecycleScope.launch {
            settingsViewModel.hasCompletedOnboarding.first() // Await its first value
            isInitialDataLoaded = true // Signal that data is loaded
        }

        setContent {
            val language by settingsViewModel.selectedLanguage.collectAsState()
            val scaleFactor by settingsViewModel.selectedAppFontScale.collectAsState()
            val soundMode by settingsViewModel.soundMode.collectAsState()

            MalankaraOrthodoxLiturgicaTheme(language = language, textScale = scaleFactor) {
                // 1. Remember the SnackbarHostState and a coroutine scope.
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                // 2. Collect the state from the manager.
                val updateDownloaded by inAppUpdateManager.updateDownloaded.collectAsState()

                // 3. Use LaunchedEffect to react to the state change.
                LaunchedEffect(updateDownloaded) {
                    if (updateDownloaded) {
                        scope.launch {
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
                }

                LaunchedEffect(soundMode) {
                    Log.d("SoundMode MainActivity", "SoundMode changed $soundMode, previousInterruptionFilter: $previousInterruptionFilter")
                    if (previousInterruptionFilter == null) {
                        previousInterruptionFilter = SoundModeManager.checkPreviousFilterState(applicationContext)
                    }
                    if (previousInterruptionFilter != true) {
                        SoundModeManager.applyAppSoundMode(applicationContext, soundMode, true)
                    }
                }

                // 5. Use Scaffold to provide a host for the Snackbar.
                Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
                    // Your NavGraph is placed inside the Scaffold's content area.
                    // The innerPadding can be passed to your NavGraph if needed to prevent overlap.
                    NavGraph(
                        settingsViewModel = settingsViewModel,
                        Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // The selected code from the Canvas is used here.
        // It checks if an update was already downloaded while the app was in the background.
        inAppUpdateManager.resumeUpdate()
        // Cancel pending restore sound work if any
        workManager.cancelUniqueWork("restore_sound_mode")

        val soundMode = settingsViewModel.soundMode.value
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        settingsViewModel.setDndPermissionStatus(notificationManager.isNotificationPolicyAccessGranted)
        if (previousInterruptionFilter != true) {
            SoundModeManager.applyAppSoundMode(applicationContext, soundMode, true)
        }
    }

    override fun onPause() {
        super.onPause()
        inAppUpdateManager.unregisterListener()
        // Schedule sound restoration when app goes to background
        if (previousInterruptionFilter != true) {
            scheduleSoundModeRestore()
        }
    }

    fun scheduleSoundModeRestore() {
        val delayTime = settingsViewModel.soundRestoreDelay.value
        val restoreWork =
            OneTimeWorkRequestBuilder<RestoreSoundWorker>()
                .setInitialDelay(delayTime.toLong(), TimeUnit.MINUTES)
                .build()

        workManager.enqueueUniqueWork(
            "restore_sound_mode",
            ExistingWorkPolicy.REPLACE,
            restoreWork,
        )
    }
}
