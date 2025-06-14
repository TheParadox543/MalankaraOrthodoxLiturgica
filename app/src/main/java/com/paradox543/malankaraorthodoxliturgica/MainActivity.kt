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
import com.paradox543.malankaraorthodoxliturgica.navigation.NavGraph
import com.paradox543.malankaraorthodoxliturgica.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val settingsViewModel: SettingsViewModel by viewModels()
    val navViewModel: NavViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep splash screen active till backend is loaded
        var isInitialDataLoaded by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isInitialDataLoaded }

        lifecycleScope.launch {
            settingsViewModel.hasCompletedOnboarding.first() // Await its first value
            navViewModel.getInitialNode() // Ensure initial node logic is run and ready
            isInitialDataLoaded = true
        }

        setContent {
            MalankaraOrthodoxLiturgicaTheme {
                NavGraph(
                    settingsViewModel,
                    navViewModel,
                )
            }
        }
    }
}