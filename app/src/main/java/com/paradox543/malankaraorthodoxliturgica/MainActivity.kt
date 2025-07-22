package com.paradox543.malankaraorthodoxliturgica

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
                NavGraph(
                    settingsViewModel = settingsViewModel,
                    navViewModel = navViewModel,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume the update flow in case the user backgrounded the app
        // after the download started.
        inAppUpdateManager.resumeUpdate(this)
    }

    override fun onPause() {
        super.onPause()
        // Clean up the listener when the activity is not in the foreground to prevent leaks.
        inAppUpdateManager.unregisterListener()
    }
}