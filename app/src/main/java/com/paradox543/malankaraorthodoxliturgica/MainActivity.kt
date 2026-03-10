package com.paradox543.malankaraorthodoxliturgica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkOut
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.zIndex
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.core.ui.theme.MalankaraOrthodoxLiturgicaTheme
import com.paradox543.malankaraorthodoxliturgica.qr.QrFabScan
import com.paradox543.malankaraorthodoxliturgica.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.ui.StartupState
import com.paradox543.malankaraorthodoxliturgica.ui.components.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.navigation.NavGraph
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SettingsViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.StartupViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                    val onboardingCompleted = s.onboardingCompleted
                    val soundMode by settingsViewModel.soundMode.collectAsState()
                    val textScale by settingsViewModel.fontScale.collectAsState()
                    val language by settingsViewModel.selectedLanguage.collectAsState()

                    MalankaraOrthodoxLiturgicaTheme(
                        language = language,
                        textScale = textScale,
                    ) {
                        // NavController hoisted here so bars can reference it
                        val navController = rememberNavController()
                        val snackbarHostState = remember { SnackbarHostState() }
                        val updateDownloaded by inAppUpdateManager.updateDownloaded.collectAsState()

                        // Tracks which bars/FAB each screen requests
                        var scaffoldUiState by remember { mutableStateOf<ScaffoldUiState>(ScaffoldUiState.None) }

                        LaunchedEffect(updateDownloaded) {
                            if (updateDownloaded) {
                                val result = snackbarHostState.showSnackbar(
                                    message = "An update has just been downloaded.",
                                    actionLabel = "RESTART",
                                    duration = SnackbarDuration.Indefinite,
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    inAppUpdateManager.completeUpdate()
                                }
                            }
                        }
                        LaunchedEffect(soundMode) {
                            soundModeManager.apply(soundMode)
                        }

                        // Apply nestedScroll modifier only for PrayerReading state
                        val scaffoldModifier = when (val state = scaffoldUiState) {
                            is ScaffoldUiState.PrayerReading ->
                                Modifier.nestedScroll(state.nestedScrollConnection)
                            else -> Modifier
                        }

                        Scaffold(
                            modifier = scaffoldModifier,
                            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                            topBar = {
                                when (val state = scaffoldUiState) {
                                    is ScaffoldUiState.Standard -> {
                                        TopNavBar(state.title, navController)
                                    }
                                    is ScaffoldUiState.PrayerReading -> {
                                        AnimatedVisibility(
                                            visible = state.isVisible,
                                            modifier = Modifier.zIndex(1f),
                                        ) {
                                            TopNavBar(state.title, navController)
                                        }
                                    }
                                    ScaffoldUiState.None -> {}
                                }
                            },
                            bottomBar = {
                                when (val state = scaffoldUiState) {
                                    is ScaffoldUiState.Standard -> {
                                        if (state.showBottomBar) {
                                            BottomNavBar(navController)
                                        }
                                    }
                                    is ScaffoldUiState.PrayerReading -> {
                                        AnimatedVisibility(
                                            visible = state.isVisible,
                                            modifier = Modifier.zIndex(1f),
                                        ) {
                                            SectionNavBar(
                                                navController = navController,
                                                prevNodeRoute = state.prevRoute,
                                                nextNodeRoute = state.nextRoute,
                                                routeProvider = state.routeProvider,
                                            )
                                        }
                                    }
                                    ScaffoldUiState.None -> {}
                                }
                            },
                            floatingActionButton = {
                                when (val state = scaffoldUiState) {
                                    is ScaffoldUiState.PrayerReading -> {
                                        if (state.showFab) {
                                            AnimatedVisibility(
                                                visible = state.isVisible,
                                                enter = fadeIn(),
                                                exit = shrinkOut(),
                                            ) {
                                                QrFabScan(navController)
                                            }
                                        }
                                    }
                                    is ScaffoldUiState.Standard -> {
                                        if (state.showBottomBar) {
                                            QrFabScan(navController)
                                        }
                                    }
                                    ScaffoldUiState.None -> {}
                                }
                            },
                        ) { innerPadding ->
                            NavGraph(
                                navController = navController,
                                onboardingCompleted = onboardingCompleted,
                                inAppReviewManager = inAppReviewManager,
                                analyticsService = analyticsService,
                                shareService = shareService,
                                settingsViewModel = settingsViewModel,
                                contentPadding = innerPadding,
                                onScaffoldStateChanged = { scaffoldUiState = it },
                            )
                        }
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
