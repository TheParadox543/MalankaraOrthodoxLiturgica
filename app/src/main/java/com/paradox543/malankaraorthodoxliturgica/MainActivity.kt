package com.paradox543.malankaraorthodoxliturgica

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import com.paradox543.malankaraorthodoxliturgica.data.repository.InAppUpdateManager
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

    // Initialize ViewModels needed for startup logic.
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val navViewModel: NavViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Check for app updates as soon as the app starts.
        inAppUpdateManager.checkForUpdate(this)

        // Keep the splash screen active until the initial data is loaded.
        var isInitialDataLoaded by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isInitialDataLoaded }

        // Launch a coroutine to load necessary data before hiding the splash screen.
        lifecycleScope.launch {
            settingsViewModel.hasCompletedOnboarding.first() // Await its first value
            navViewModel.getInitialNode() // Ensure initial node logic is run and ready
            isInitialDataLoaded = true // Signal that data is loaded
        }

        setContent {
            MalankaraOrthodoxLiturgicaTheme {
                // 1. Remember the SnackbarHostState and a coroutine scope.
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                // 2. Collect the state from the manager.
                val updateDownloaded by inAppUpdateManager.updateDownloaded.collectAsState()

                // 3. Use LaunchedEffect to react to the state change.
                LaunchedEffect(updateDownloaded) {
                    if (updateDownloaded) {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "An update has just been downloaded.",
                                actionLabel = "RESTART",
                                duration = SnackbarDuration.Indefinite // Stays until dismissed or actioned
                            )
                            // 4. Perform action based on user interaction.
                            if (result == SnackbarResult.ActionPerformed) {
                                inAppUpdateManager.completeUpdate()
                            }
                        }
                    }
                }

                // 5. Use Scaffold to provide a host for the Snackbar.
                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    // Your NavGraph is placed inside the Scaffold's content area.
                    // The innerPadding can be passed to your NavGraph if needed to prevent overlap.
                    NavGraph(
                        Modifier.padding(innerPadding),
                        settingsViewModel = settingsViewModel,
                        navViewModel = navViewModel,
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
    }

    override fun onPause() {
        super.onPause()
        inAppUpdateManager.unregisterListener()
    }
}