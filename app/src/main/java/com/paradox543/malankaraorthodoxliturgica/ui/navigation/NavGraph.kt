package com.paradox543.malankaraorthodoxliturgica.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkOut
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.paradox543.malankaraorthodoxliturgica.MainActivity
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.qr.QrFabScan
import com.paradox543.malankaraorthodoxliturgica.qr.QrScannerView
import com.paradox543.malankaraorthodoxliturgica.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.ui.components.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.components.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.ui.screens.AboutScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.BibleBookScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.BibleChapterScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.BibleReadingScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.BibleScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.CalendarScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.ContentNotReadyScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.OnboardingScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.PrayNowScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.SectionScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.SettingsScreen
import com.paradox543.malankaraorthodoxliturgica.ui.screens.SongScreen
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.CalendarViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SettingsViewModel

/**
 * App Compose root. Owns [NavController], the single [Scaffold], and navigation state.
 * [MainActivity] is a thin Android entry point that sets the theme and passes platform
 * services/managers here.
 */
@Composable
fun NavGraph(
    onboardingCompleted: Boolean,
    inAppUpdateManager: InAppUpdateManager,
    inAppReviewManager: InAppReviewManager,
    analyticsService: AnalyticsService,
    shareService: ShareService,
    settingsViewModel: SettingsViewModel,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val updateDownloaded by inAppUpdateManager.updateDownloaded.collectAsState()

    // Tracks which bars/FAB each screen requests
    var scaffoldUiState by remember { mutableStateOf<ScaffoldUiState>(ScaffoldUiState.None) }

    // Show update snackbar when a new version has been downloaded
    LaunchedEffect(updateDownloaded) {
        if (updateDownloaded) {
            val result =
                snackbarHostState.showSnackbar(
                    message = "An update has just been downloaded.",
                    actionLabel = "RESTART",
                    duration = SnackbarDuration.Indefinite,
                )
            if (result == SnackbarResult.ActionPerformed) {
                inAppUpdateManager.completeUpdate()
            }
        }
    }

    val bibleViewModel: BibleViewModel = hiltViewModel()

    // Observe the current route to pass to bars for highlight/back logic
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Log screen views for analytics
    DisposableEffect(navController, analyticsService) {
        val listener =
            NavController.OnDestinationChangedListener { _, destination, args ->
                val argsMap =
                    args?.keySet()?.associateWith { key ->
                        args.get(key)?.toString()
                    } ?: emptyMap()
                analyticsService.logScreenVisited(destination.route ?: "", argsMap)
            }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    // Apply nestedScroll modifier only for PrayerReading state
    val scaffoldModifier =
        when (val state = scaffoldUiState) {
            is ScaffoldUiState.PrayerReading -> Modifier.nestedScroll(state.nestedScrollConnection)
            else -> Modifier
        }

    Scaffold(
        modifier = scaffoldModifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            when (val state = scaffoldUiState) {
                is ScaffoldUiState.Standard -> {
                    TopNavBar(
                        title = state.title,
                        currentRoute = currentRoute,
                        onBack = { navController.navigateUp() },
                        onSettingsClick = { navController.navigate(AppScreen.Settings.route) },
                    )
                }

                is ScaffoldUiState.PrayerReading -> {
                    AnimatedVisibility(
                        visible = state.isVisible,
                        modifier = Modifier.zIndex(1f),
                    ) {
                        TopNavBar(
                            title = state.title,
                            currentRoute = currentRoute,
                            onBack = { navController.navigateUp() },
                            onSettingsClick = { navController.navigate(AppScreen.Settings.route) },
                        )
                    }
                }

                ScaffoldUiState.None -> {}
            }
        },
        bottomBar = {
            when (val state = scaffoldUiState) {
                is ScaffoldUiState.Standard -> {
                    if (state.showBottomBar) {
                        BottomNavBar(
                            currentRoute = currentRoute,
                            onNavItemClick = { route ->
                                navController.navigate(route) {
                                    navController.popBackStack(route, inclusive = true)
                                }
                            },
                        )
                    }
                }

                is ScaffoldUiState.PrayerReading -> {
                    AnimatedVisibility(
                        visible = state.isVisible,
                        modifier = Modifier.zIndex(1f),
                    ) {
                        SectionNavBar(
                            prevNodeRoute = state.prevRoute,
                            nextNodeRoute = state.nextRoute,
                            routeProvider = state.routeProvider,
                            onPrevClick = {
                                navController.navigate(state.prevRoute!!) {
                                    navController.popBackStack()
                                }
                            },
                            onNextClick = {
                                navController.navigate(state.nextRoute!!) {
                                    navController.popBackStack()
                                }
                            },
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
                            QrFabScan(
                                onScanClick = { navController.navigate(AppScreen.QrScanner.route) },
                            )
                        }
                    }
                }

                is ScaffoldUiState.Standard -> {
                    if (state.showBottomBar) {
                        QrFabScan(
                            onScanClick = { navController.navigate(AppScreen.QrScanner.route) },
                        )
                    }
                }

                ScaffoldUiState.None -> {}
            }
        },
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination =
                if (onboardingCompleted) {
                    AppScreen.Home.route
                } else {
                    AppScreen.Onboarding.route
                },
        ) {
            composable(
                AppScreen.Home.route,
                deepLinks = AppScreen.Home.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
            ) {
                val prayerViewModel: PrayerViewModel = hiltViewModel()
                val prayerNavViewModel: PrayerNavViewModel = hiltViewModel()
                HomeScreen(
                    navController,
                    prayerViewModel,
                    prayerNavViewModel,
                    inAppReviewManager,
                    innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(AppScreen.Onboarding.route) {
                val prayerViewModel: PrayerViewModel = hiltViewModel()
                OnboardingScreen(
                    { _, _ -> },
                    {
                        navController.navigate(AppScreen.Home.route) {
                            popUpTo(AppScreen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    },
                    settingsViewModel,
                    prayerViewModel,
                    innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(
                route = AppScreen.Section.route,
                arguments =
                    listOf(
                        navArgument(AppScreen.Section.ARG_ROUTE) {
                            type = NavType.StringType
                        },
                    ),
                deepLinks = AppScreen.Section.DEEP_LINK_PATTERN.let { listOf(navDeepLink { uriPattern = it }) },
            ) { backStackEntry ->
                val prayerNavViewModel: PrayerNavViewModel = hiltViewModel(backStackEntry)
                val prayerViewModel: PrayerViewModel = hiltViewModel(backStackEntry)
                val route = backStackEntry.arguments?.getString(AppScreen.Section.ARG_ROUTE) ?: ""
                val node = prayerNavViewModel.findNode(route)
                if (node != null) {
                    SectionScreen(
                        navController,
                        prayerViewModel,
                        node,
                        inAppReviewManager,
                        innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState = it },
                    )
                } else {
                    ContentNotReadyScreen(
                        navController,
                        message = route,
                        contentPadding = innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState = it },
                    )
                }
            }

            composable(
                route = AppScreen.Prayer.route,
                arguments =
                    listOf(
                        navArgument(AppScreen.Prayer.ARG_ROUTE) {
                            type = NavType.StringType
                        },
                    ),
                deepLinks = AppScreen.Prayer.DEEP_LINK_PATTERN.let { listOf(navDeepLink { uriPattern = it }) },
            ) { backStackEntry ->
                val prayerViewModel: PrayerViewModel = hiltViewModel(backStackEntry)
                val prayerNavViewModel: PrayerNavViewModel = hiltViewModel(backStackEntry)
                val prayerRoute = backStackEntry.arguments?.getString(AppScreen.Prayer.ARG_ROUTE) ?: ""
                val scrollIndex =
                    backStackEntry.arguments?.getString(AppScreen.Prayer.ARG_SCROLL)?.toIntOrNull() ?: 0
                val node = prayerNavViewModel.findNode(prayerRoute)
                if (node != null) {
                    PrayerScreen(
                        { route, replace ->
                            navController.navigate(AppScreen.Prayer.createRoute(route)) {
                                if (replace) {
                                    navController.popBackStack()
                                }
                            }
                        },
                        prayerViewModel,
                        settingsViewModel,
                        prayerNavViewModel,
                        node,
                        scrollIndex,
                        innerPadding,
                    ) { scaffoldUiState = it }
                } else {
                    ContentNotReadyScreen(
                        navController,
                        message = prayerRoute,
                        contentPadding = innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState = it },
                    )
                }
            }

            composable(
                route = AppScreen.Song.route,
                arguments =
                    listOf(
                        navArgument(AppScreen.Song.ARG_ROUTE) {
                            type = NavType.StringType
                        },
                    ),
            ) { backStackEntry ->
                val prayerNavViewModel: PrayerNavViewModel = hiltViewModel(backStackEntry)
                val route = backStackEntry.arguments?.getString(AppScreen.Song.ARG_ROUTE) ?: ""
                val node = prayerNavViewModel.findNode(route)
                if (node != null) {
                    SongScreen(
                        songFilename = node.filename ?: "",
                        contentPadding = innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState = it },
                    )
                } else {
                    ContentNotReadyScreen(
                        navController,
                        message = route,
                        contentPadding = innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState = it },
                    )
                }
            }

            composable(AppScreen.PrayNow.route) { backStackEntry ->
                val prayerViewModel = hiltViewModel<PrayerViewModel>(backStackEntry)
                val prayerNavViewModel = hiltViewModel<PrayerNavViewModel>(backStackEntry)
                PrayNowScreen(
                    { route ->
                        navController.navigate(AppScreen.Prayer.createRoute(route))
                    },
                    prayerViewModel,
                    prayerNavViewModel,
                    innerPadding,
                ) { scaffoldUiState = it }
            }

            composable(
                AppScreen.Bible.route,
                deepLinks = AppScreen.Bible.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
            ) { backStackEntry ->
                val bibleViewModel: BibleViewModel = hiltViewModel(backStackEntry)
                BibleScreen(
                    { index ->
                        navController.navigate(AppScreen.BibleBook.createRoute(index))
                    },
                    settingsViewModel,
                    bibleViewModel,
                    innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(
                route = AppScreen.BibleBook.route,
                arguments =
                    listOf(
                        navArgument(AppScreen.BibleBook.ARG_BOOK_INDEX) {
                            type = NavType.StringType
                        },
                    ),
                deepLinks = AppScreen.BibleBook.DEEP_LINK_PATTERN.let { listOf(navDeepLink { uriPattern = it }) },
            ) { backStackEntry ->
                val bibleViewModel: BibleViewModel = hiltViewModel(backStackEntry)
                val bookIndex =
                    backStackEntry.arguments?.getString(AppScreen.BibleBook.ARG_BOOK_INDEX)?.toIntOrNull()
                        ?: 0
                BibleBookScreen(
                    { bookIndex, chapterIndex ->
                        navController.navigate(AppScreen.BibleChapter.createRoute(bookIndex, chapterIndex))
                    },
                    settingsViewModel,
                    bibleViewModel,
                    bookIndex,
                    innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(
                route = AppScreen.BibleChapter.route,
                arguments =
                    listOf(
                        navArgument(AppScreen.BibleChapter.ARG_BOOK_INDEX) {
                            type = NavType.StringType
                        },
                    ),
                deepLinks =
                    AppScreen.BibleChapter.DEEP_LINK_PATTERN.let { listOf(navDeepLink { uriPattern = it }) }
                        ?: emptyList(),
            ) { backStackEntry ->
                val bibleViewModel: BibleViewModel = hiltViewModel(backStackEntry)
                val bookIndex =
                    backStackEntry.arguments
                        ?.getString(AppScreen.BibleChapter.ARG_BOOK_INDEX)
                        ?.toIntOrNull() ?: 0
                val chapterIndex =
                    backStackEntry.arguments
                        ?.getString(AppScreen.BibleChapter.ARG_CHAPTER_INDEX)
                        ?.toIntOrNull() ?: 0
                BibleChapterScreen(
                    settingsViewModel,
                    bibleViewModel,
                    bookIndex,
                    chapterIndex,
                    innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(
                AppScreen.Calendar.route,
                deepLinks = AppScreen.Calendar.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
            ) { backStackEntry ->
                val calendarViewModel: CalendarViewModel = hiltViewModel(backStackEntry)
                CalendarScreen(
                    navController,
                    bibleViewModel,
                    calendarViewModel,
                    contentPadding = innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(AppScreen.BibleReader.route) {
                BibleReadingScreen(
                    { _, _ -> },
                    bibleViewModel,
                    settingsViewModel,
                    innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(AppScreen.QrScanner.route) {
                QrScannerView(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            navController.popBackStack(AppScreen.QrScanner.route, inclusive = true)
                        }
                    },
                    onBack = { navController.navigateUp() },
                    contentPadding = innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(
                AppScreen.Settings.route,
                deepLinks = AppScreen.Settings.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
            ) {
                SettingsScreen(
                    onNavigateToAbout = { navController.navigate(AppScreen.About.route) },
                    settingsViewModel = settingsViewModel,
                    shareService = shareService,
                    contentPadding = innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(
                AppScreen.About.route,
                deepLinks = AppScreen.About.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
            ) {
                AboutScreen(
                    innerPadding,
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }
        }
    }
}
