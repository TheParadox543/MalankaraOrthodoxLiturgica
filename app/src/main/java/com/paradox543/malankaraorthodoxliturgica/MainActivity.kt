package com.paradox543.malankaraorthodoxliturgica

import android.app.NotificationManager
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
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
import com.paradox543.malankaraorthodoxliturgica.data.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.data.repository.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.data.repository.LiturgicalCalendarRepository
import com.paradox543.malankaraorthodoxliturgica.navigation.NavGraph
import com.paradox543.malankaraorthodoxliturgica.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Inject the update manager for handling in-app updates.
    @Inject
    lateinit var inAppUpdateManager: InAppUpdateManager

    @Inject
    lateinit var calendarRepository: LiturgicalCalendarRepository

    // Initialize ViewModels needed for startup logic.
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val navViewModel: NavViewModel by viewModels()

    private fun hasGrantedDndPermission(): Boolean {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        settingsViewModel.setDndPermissionStatus(notificationManager.isNotificationPolicyAccessGranted)
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun requestDndPermission() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Toast.makeText(this, "Please grant DND permission in Settings.", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Toast
                .makeText(
                    this,
                    "DND permissions not granted in Settings. Please enable to make use of features.",
                    Toast.LENGTH_LONG,
                ).show()
            return
        }
    }

    fun setDndMode(enable: Boolean) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as? NotificationManager ?: return
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(
                when (enable) {
                    true -> {
                        NotificationManager.INTERRUPTION_FILTER_NONE
                    }
                    false -> {
                        NotificationManager.INTERRUPTION_FILTER_ALL
                    }
                },
            )
        }
    }

    private fun setSilentMode() {
        if (!hasGrantedDndPermission()) return
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
    }

    private fun restoreNormalMode() {
        if (!hasGrantedDndPermission()) return
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        setDndMode(false)
    }

    private fun setSilentOnChange(soundMode: SoundMode) {
        if (!hasGrantedDndPermission()) return
        when (soundMode) {
            SoundMode.OFF -> {
                restoreNormalMode()
            }

            SoundMode.SILENT -> {
                setSilentMode()
            }

            SoundMode.DND -> {
                setDndMode(true)
            }
        }
    }

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
            navViewModel.getInitialNode() // Ensure initial node logic is run and ready
            calendarRepository.initialize() // Load calendar data
            isInitialDataLoaded = true // Signal that data is loaded
        }

        setContent {
            val language by settingsViewModel.selectedLanguage.collectAsState()
            val scaleFactor by settingsViewModel.selectedFontScale.collectAsState()
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
                    if (soundMode != SoundMode.OFF) {
                        requestDndPermission()
                    }
                    restoreNormalMode()
                    setSilentOnChange(soundMode)
                }

                // 5. Use Scaffold to provide a host for the Snackbar.
                Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
                    // Your NavGraph is placed inside the Scaffold's content area.
                    // The innerPadding can be passed to your NavGraph if needed to prevent overlap.
                    NavGraph(
                        settingsViewModel = settingsViewModel,
                        navViewModel = navViewModel,
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
        val soundMode = settingsViewModel.soundMode.value
        setSilentOnChange(soundMode)
    }

    override fun onPause() {
        super.onPause()
        inAppUpdateManager.unregisterListener()
        val soundMode = settingsViewModel.soundMode.value
        when (soundMode) {
            SoundMode.OFF -> {} // do nothing
            SoundMode.SILENT -> restoreNormalMode()
            SoundMode.DND -> setDndMode(false)
        }
    }
}