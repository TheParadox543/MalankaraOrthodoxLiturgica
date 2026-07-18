package com.paradox543.malankaraorthodoxliturgica

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.AndroidUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.model.UpdateType
import com.paradox543.malankaraorthodoxliturgica.core.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.OnboardingStage
import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.StartupState
import com.paradox543.malankaraorthodoxliturgica.ui.navigation.NavGraph
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.ui.navigation.AppScreen
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.StartupViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Thin Android entry point. Responsible for:
 * - Splash screen management
 * - In-app update/review lifecycle hooks
 * - Sound mode management
 * - Theme setup
 *
 * All navigation and UI logic lives in [NavGraph].
 */
class MainActivity : ComponentActivity() {
    private val androidUpdateManager: AndroidUpdateManager by inject()

    private val analyticsService: AnalyticsService by inject()

    private val shareService: ShareService by inject()

    private val soundModeManager: SoundModeManager by inject()

    private val settingsViewModel: SettingsViewModel by viewModel()
    private val startupViewModel: StartupViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        androidUpdateManager.bindActivity(this)

        // Prewarm Bible metadata cache on background thread
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val bibleRepository: BibleRepository by inject() // Koin inject
                bibleRepository.loadBibleMetaData() // Triggers lazy runBlocking once
            } catch (e: Exception) {
                // Log if needed, but don't crash startup
                android.util.Log.w("MainActivity", "Bible metadata prewarm failed", e)
            }
        }

        var keepSplashOn by mutableStateOf(true)
        splashScreen.setKeepOnScreenCondition { keepSplashOn }

        lifecycleScope.launch {
            startupViewModel.startupState.collect { state ->
                if (state is StartupState.Ready) keepSplashOn = false
            }
        }

        application.registerActivityLifecycleCallbacks(
            object : Application.ActivityLifecycleCallbacks {
                override fun onActivityResumed(activity: Activity) {
                    ActivityHolder.activity = activity
                }

                override fun onActivityPaused(activity: Activity) {
                    if (ActivityHolder.activity == activity) {
                        ActivityHolder.activity = null
                    }
                }

                override fun onActivityCreated(
                    a: Activity,
                    b: Bundle?,
                ) {}

                override fun onActivityStarted(a: Activity) {}

                override fun onActivityStopped(a: Activity) {}

                override fun onActivitySaveInstanceState(
                    a: Activity,
                    b: Bundle,
                ) {}

                override fun onActivityDestroyed(a: Activity) {}
            },
        )

        setContent {
            val startupState by startupViewModel.startupState.collectAsState()
            when (val s = startupState) {
                is StartupState.Loading -> {}

                is StartupState.Ready -> {
                    val soundMode by settingsViewModel.soundMode.collectAsState()
                    val textScale by settingsViewModel.fontScale.collectAsState()
                    val language by settingsViewModel.selectedLanguage.collectAsState()

                    var showNewFeaturesDialog by remember {
                        mutableStateOf(s.onboardingStage > 0 && s.onboardingStage < OnboardingStage.COMPLETE.value)
                    }
                    var navigateToOnboarding by remember { mutableStateOf<((NavController) -> Unit)?>(null) }

                    // Android system concern: apply the user's preferred sound/DnD mode
                    LaunchedEffect(soundMode) {
                        soundModeManager.apply(soundMode)
                    }

                    MalankaraOrthodoxLiturgicaTheme(
                        language = language,
                        textScale = textScale,
                    ) {
                        NavGraph(
                            onboardingStage = s.onboardingStage,
                            appUpdateManager = androidUpdateManager,
                            analyticsService = analyticsService,
                            shareService = shareService,
                            settingsViewModel = settingsViewModel,
                            onNavigateToOnboarding = navigateToOnboarding,
                        )

                        if (showNewFeaturesDialog) {
                            AlertDialog(
                                onDismissRequest = { showNewFeaturesDialog = false },
                                title = { Text("New Features") },
                                text = { Text("New features have been added since your last update. Would you like a quick tour?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showNewFeaturesDialog = false
                                            navigateToOnboarding = { navController ->
                                                navController.navigate(AppScreen.Onboarding.route)
                                            }
                                        },
                                    ) {
                                        Text("Show Me")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            showNewFeaturesDialog = false
                                            // Mark as complete so they don't see it again
                                            settingsViewModel.setOnboardingCompleted(true)
                                        },
                                    ) {
                                        Text("Not Now")
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        androidUpdateManager.bindActivity(this)
        androidUpdateManager.onResume()
        lifecycleScope.launch {
            val updateInfo = androidUpdateManager.checkForUpdate()
            if (updateInfo?.isUpdateAvailable == true) {
                val updateType = if (updateInfo.isForceUpdate) UpdateType.IMMEDIATE else UpdateType.FLEXIBLE
                androidUpdateManager.startUpdate(updateType)
            }
        }
        settingsViewModel.refreshDndPermissionStatus()
        soundModeManager.cancelRestoreWork()
        val soundMode = settingsViewModel.soundMode.value
        soundModeManager.apply(soundMode)
    }

    override fun onPause() {
        super.onPause()
        androidUpdateManager.onPause()
        soundModeManager.scheduleRestore(settingsViewModel.soundRestoreDelay.value)
    }
}
