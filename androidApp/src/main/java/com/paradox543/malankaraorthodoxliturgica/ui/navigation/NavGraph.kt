package com.paradox543.malankaraorthodoxliturgica.ui.navigation

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.paradox543.malankaraorthodoxliturgica.MainActivity
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.AppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.Synchronizer
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.SyncStatus
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.QrFabScan
import com.paradox543.malankaraorthodoxliturgica.core.ui.modifier.globalPinchZoom
import com.paradox543.malankaraorthodoxliturgica.core.ui.navigation.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.core.ui.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.core.ui.navigation.navItems
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.core.ui.screens.ContentNotReadyScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleBookScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleChapterScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens.BibleReadingScreen
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens.CalendarLiturgicalSeasonScreen
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens.OnboardingScreen
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.IndexScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayNowScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.SectionScreen
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.settings.screens.AboutScreen
import com.paradox543.malankaraorthodoxliturgica.feature.settings.screens.SettingsScreen
import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.song.screens.SongScreen
import com.paradox543.malankaraorthodoxliturgica.feature.song.viewmodel.SongPlayerViewModel
import com.paradox543.malankaraorthodoxliturgica.qr.QrScannerView
import org.koin.compose.viewmodel.koinViewModel

/**
 * App Compose root. Owns [NavController], the single [Scaffold], and navigation state.
 * [MainActivity] is a thin Android entry point that sets the theme and passes platform
 * services/managers here.
 */
@Composable
fun NavGraph(
    onboardingStage: Int,
    appUpdateManager: AppUpdateManager,
    analyticsService: AnalyticsService,
    shareService: ShareService,
    settingsViewModel: SettingsViewModel,
    onNavigateToOnboarding: ((NavController) -> Unit)? = null,
) {
    val synchronizer: Synchronizer = org.koin.compose.koinInject()
    val syncState by synchronizer.syncState.collectAsState()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val updateDownloaded by appUpdateManager.updateReady.collectAsState()
    val context: Context = LocalContext.current

    // Only force onboarding for brand-new users (Stage 0: Welcome)
    val onboardingCompleted = onboardingStage > 0

    LaunchedEffect(onNavigateToOnboarding) {
        onNavigateToOnboarding?.invoke(navController)
    }

    // Tracks which bars/FAB each screen requests
    val scaffoldUiState = remember { mutableStateOf<ScaffoldUiState>(ScaffoldUiState.None) }

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
                appUpdateManager.completeUpdate()
            }
        }
    }

    // Show sync snackbar when content has been updated in background
    LaunchedEffect(syncState) {
        if (syncState.status == SyncStatus.SUCCESS && syncState.hasUpdate) {
            snackbarHostState.showSnackbar(
                message = "Content has been updated.",
                duration = SnackbarDuration.Short
            )
        }
    }

    // Observe the current route to pass to bars for highlight/back logic
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Log screen views for analytics
    DisposableEffect(navController, analyticsService) {
        val listener =
            NavController.OnDestinationChangedListener { _, destination, args ->
                val argsMap =
                    args?.keySet()?.associateWith { key ->
                        args.readNavArgAsString(key, destination.arguments[key]?.type)
                    } ?: emptyMap()
                analyticsService.logEvent(AnalyticsEvent.ScreenVisited(destination.route ?: "", argsMap))
            }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }

    val pinchZoomDisabledRoutes =
        setOf(
            AppScreen.QrScanner.route,
            AppScreen.Onboarding.route,
        )

    val pinchZoomEnabled = currentRoute !in pinchZoomDisabledRoutes

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val showNavSuite =
        when (val state = scaffoldUiState.value) {
            is ScaffoldUiState.Standard -> state.showBottomBar
            else -> false
        }

    val windowSize = adaptiveInfo.windowSizeClass
    val isCompactHeight = windowSize.windowHeightSizeClass == WindowHeightSizeClass.COMPACT
    val isExpandedWidth = windowSize.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    val isSideNavVisible = showNavSuite && (isCompactHeight || isExpandedWidth)
    val isDrawerMode = showNavSuite && isExpandedWidth && !isCompactHeight
    val isRailMode = showNavSuite && isCompactHeight
    val isBottomBarMode = showNavSuite && !isSideNavVisible

    // Create ViewModels once at NavGraph level to prevent recreation glitches
    val prayerViewModel: PrayerViewModel = koinViewModel()
    val prayerNavViewModel: PrayerNavViewModel = koinViewModel()
    val songPlayerViewModel: SongPlayerViewModel = koinViewModel()
    val bibleViewModel: BibleViewModel = koinViewModel()
    val calendarViewModel: CalendarViewModel = koinViewModel()
    val prayerRootNode by prayerNavViewModel.rootNode.collectAsState()
    val isPrayerTreeLoaded = prayerRootNode.children.isNotEmpty()

    // Apply nestedScroll modifier only for PrayerReading state
    val baseScaffoldModifier =
        when (val state = scaffoldUiState.value) {
            is ScaffoldUiState.PrayerReading -> Modifier.nestedScroll(state.nestedScrollConnection)
            else -> Modifier
        }

    val scaffoldModifier =
        baseScaffoldModifier.globalPinchZoom(
            enabled = pinchZoomEnabled,
            onZoomInStep = { settingsViewModel.setFontScaleDebounced(1) },
            onZoomOutStep = { settingsViewModel.setFontScaleDebounced(-1) },
        )

    val systemBottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val dynamicBarHeight = 65.dp + systemBottomInset

    val bottomBarItemColors =
        NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
        )

    val railItemColors =
        NavigationRailItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
        )

    val drawerItemColors =
        NavigationDrawerItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
            unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
            selectedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
            unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
        )

    @Composable
    fun AppScaffold() {
        Scaffold(
            modifier = scaffoldModifier,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                if (!isCompactHeight) {
                    when (val state = scaffoldUiState.value) {
                        is ScaffoldUiState.Standard -> {
                            TopNavBar(
                                title = state.title,
                                showBack = currentRoute != AppScreen.Home.route,
                                showSettings = !isSideNavVisible && currentRoute != AppScreen.Settings.route,
                                onBack = { navController.navigateUp() },
                            ) { navController.navigate(AppScreen.Settings.route) }
                        }

                        is ScaffoldUiState.PrayerReading -> {
                            AnimatedVisibility(
                                visible = state.isVisible,
                                modifier = Modifier.zIndex(1f),
                            ) {
                                TopNavBar(
                                    title = state.title,
                                    showBack = currentRoute != AppScreen.Home.route,
                                    showSettings = !isSideNavVisible && currentRoute != AppScreen.Settings.route,
                                    onBack = { navController.navigateUp() },
                                ) { navController.navigate(AppScreen.Settings.route) }
                            }
                        }

                        is ScaffoldUiState.BibleChapterReading -> {
                            AnimatedVisibility(
                                visible = state.isVisible,
                                modifier = Modifier.zIndex(1f),
                            ) {
                                TopNavBar(
                                    title = state.title,
                                    showBack = currentRoute != AppScreen.Home.route,
                                    showSettings = !isSideNavVisible && currentRoute != AppScreen.Settings.route,
                                    onBack = { navController.navigateUp() },
                                ) { navController.navigate(AppScreen.Settings.route) }
                            }
                        }

                        ScaffoldUiState.None -> {}
                    }
                }
            },
            bottomBar = {
                when (val state = scaffoldUiState.value) {
                    is ScaffoldUiState.Standard -> {
                        if (isBottomBarMode) {
                            NavigationBar(
                                modifier = Modifier.height(dynamicBarHeight),
                                containerColor = MaterialTheme.colorScheme.primary,
                            ) {
                                navItems.filter { !it.isRailOnly }.forEach { item ->
                                    NavigationBarItem(
                                        icon = item.icon,
                                        label = { Text(item.label) },
                                        selected = currentRoute == item.route,
                                        onClick = {
                                            navController.navigate(item.route) {
                                                navController.popBackStack(item.route, inclusive = true)
                                            }
                                        },
                                        colors = bottomBarItemColors,
                                    )
                                }
                            }
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
                                onShowQr = state.onShowQrDialog,
                                onPrevClick = {
                                    navController.navigate(state.routeProvider(state.prevRoute!!)) {
                                        navController.popBackStack()
                                    }
                                },
                                onNextClick = {
                                    navController.navigate(state.routeProvider(state.nextRoute!!)) {
                                        navController.popBackStack()
                                    }
                                },
                            )
                        }
                    }

                    is ScaffoldUiState.BibleChapterReading -> {
                        AnimatedVisibility(
                            visible = state.isVisible,
                            modifier = Modifier.zIndex(1f),
                        ) {
                            SectionNavBar(
                                prevNodeRoute = state.prevRoute?.let { "${it.bookIndex}/${it.chapterIndex}" },
                                nextNodeRoute = state.nextRoute?.let { "${it.bookIndex}/${it.chapterIndex}" },
                                onShowQr = state.onShowQrDialog,
                                onPrevClick = {
                                    navController.navigate(state.routeProvider(state.prevRoute!!)) {
                                        navController.popBackStack()
                                    }
                                },
                                onNextClick = {
                                    navController.navigate(state.routeProvider(state.nextRoute!!)) {
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
                when (val state = scaffoldUiState.value) {
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

                    is ScaffoldUiState.BibleChapterReading -> {
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
                        if (state.showFab) {
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
                    val liturgicalDay by calendarViewModel.todayLiturgicalDay.collectAsState()
                    val recommendedPrayers = prayerNavViewModel.getAllPrayerNodes()
                    val topPrayer = recommendedPrayers.firstOrNull()

                    HomeScreen(
                        prayerViewModel = prayerViewModel,
                        prayerNavViewModel = prayerNavViewModel,
                        liturgicalDay = liturgicalDay,
                        topRecommendedPrayer = topPrayer,
                        contentPadding = innerPadding,
                        onSectionNavigate = { route ->
                            navController.navigate(AppScreen.Section.createRoute(route))
                        },
                        onPrayerNavigate = { route ->
                            navController.navigate(AppScreen.Prayer.createRoute(route))
                        },
                        onSongNavigate = { route ->
                            navController.navigate(AppScreen.Song.createRoute(route))
                        },
                        onPrayNowNavigate = {
                            navController.navigate(AppScreen.PrayNow.route)
                        },
                        onIndexNavigate = {
                            navController.navigate(AppScreen.Index.route)
                        },
                        onScaffoldStateChanged = { scaffoldUiState.value = it },
                    )
                }

                composable(AppScreen.Onboarding.route) {
                    val onboardingViewModel: OnboardingViewModel = koinViewModel()
                    OnboardingScreen(
                        onboardingViewModel = onboardingViewModel,
                        contentPadding = innerPadding,
                        onNavigateToHome = {
                            navController.navigate(AppScreen.Home.route) {
                                popUpTo(AppScreen.Onboarding.route) {
                                    inclusive = true
                                }
                            }
                        },
                        requestDndPermission = {
                            val notificationManager =
                                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            if (!notificationManager.isNotificationPolicyAccessGranted) {
                                Toast
                                    .makeText(
                                        context,
                                        "Please grant the app access to modify DND in settings.",
                                        Toast.LENGTH_LONG,
                                    ).show()
                                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                                context.startActivity(intent)
                            }
                        },
                        onScaffoldStateChanged = { scaffoldUiState.value = it },
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
                    val route = backStackEntry.arguments?.getString(AppScreen.Section.ARG_ROUTE) ?: ""
                    val liturgicalDay by calendarViewModel.todayLiturgicalDay.collectAsState()
                    val node = prayerRootNode.findByRoute(route)
                    if (!isPrayerTreeLoaded) {
                        ContentLoadingScreen(
                            contentPadding = innerPadding,
                            onScaffoldStateChanged = { scaffoldUiState.value = it },
                        )
                    } else if (node != null) {
                        SectionScreen(
                            prayerViewModel,
                            prayerNavViewModel,
                            node,
                            innerPadding,
                            liturgicalDay = liturgicalDay,
                            onScaffoldStateChanged = { scaffoldUiState.value = it },
                            onSectionNavigate = { route ->
                                navController.navigate(AppScreen.Section.createRoute(route))
                            },
                            onPrayerNavigate = { route ->
                                navController.navigate(AppScreen.Prayer.createRoute(route))
                            },
                            onSongNavigate = { route ->
                                navController.navigate(AppScreen.Song.createRoute(route))
                            },
                            onIndexNavigate = {
                                navController.navigate(AppScreen.Index.route)
                            },
                        )
                    } else {
                        ContentNotReadyScreen(
                            message = route,
                            contentPadding = innerPadding,
                            onBackNavigation = { navController.navigateUp() },
                        ) { scaffoldUiState.value = it }
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
                    val prayerRoute = backStackEntry.arguments?.getString(AppScreen.Prayer.ARG_ROUTE) ?: ""
                    val scrollIndex =
                        backStackEntry.arguments?.getString(AppScreen.Prayer.ARG_SCROLL)?.toIntOrNull() ?: 0
                    val node = prayerRootNode.findByRoute(prayerRoute)
                    if (!isPrayerTreeLoaded) {
                        ContentLoadingScreen(
                            contentPadding = innerPadding,
                            onScaffoldStateChanged = { scaffoldUiState.value = it },
                        )
                    } else if (node != null) {
                        PrayerScreen(
                            { route, replace ->
                                navController.navigate(AppScreen.Prayer.createRoute(route)) {
                                    if (replace) {
                                        navController.popBackStack()
                                    }
                                }
                            },
                            prayerViewModel,
                            prayerNavViewModel,
                            node,
                            scrollIndex,
                            innerPadding,
                            onQrDialogShow = { route, scrollIndex ->
                                AppScreen.Prayer.createDeepLink(route, scrollIndex)
                            },
                            routeProvider = {
                                AppScreen.Prayer.createRoute(it)
                            },
                        ) { scaffoldUiState.value = it }
                    } else {
                        ContentNotReadyScreen(
                            message = prayerRoute,
                            contentPadding = innerPadding,
                            onBackNavigation = { navController.navigateUp() },
                        ) { scaffoldUiState.value = it }
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
                    val route = backStackEntry.arguments?.getString(AppScreen.Song.ARG_ROUTE) ?: ""
                    val node = prayerRootNode.findByRoute(route)
                    if (!isPrayerTreeLoaded) {
                        ContentLoadingScreen(
                            contentPadding = innerPadding,
                            onScaffoldStateChanged = { scaffoldUiState.value = it },
                        )
                    } else if (node != null) {
                        SongScreen(
                            songPlayerViewModel = songPlayerViewModel,
                            songFilename = node.filename ?: "",
                            contentPadding = innerPadding,
                            onScaffoldStateChanged = { scaffoldUiState.value = it },
                        )
                    } else {
                        ContentNotReadyScreen(
                            message = route,
                            contentPadding = innerPadding,
                            onBackNavigation = { navController.navigateUp() },
                        ) { scaffoldUiState.value = it }
                    }
                }

                composable(AppScreen.PrayNow.route) {
                    PrayNowScreen(
                        { route ->
                            navController.navigate(AppScreen.Prayer.createRoute(route))
                        },
                        prayerViewModel,
                        prayerNavViewModel,
                        innerPadding,
                    ) { scaffoldUiState.value = it }
                }

                composable(AppScreen.Index.route) {
                    if (!isPrayerTreeLoaded) {
                        ContentLoadingScreen(
                            contentPadding = innerPadding,
                            onScaffoldStateChanged = { scaffoldUiState.value = it },
                        )
                    } else {
                        IndexScreen(
                            prayerViewModel = prayerViewModel,
                            prayerNavViewModel = prayerNavViewModel,
                            contentPadding = innerPadding,
                            onPrayerNavigate = { route ->
                                navController.navigate(AppScreen.Prayer.createRoute(route))
                            },
                        ) { scaffoldUiState.value = it }
                    }
                }

                composable(
                    AppScreen.Bible.route,
                    deepLinks = AppScreen.Bible.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
                ) {
                    BibleScreen(
                        { index ->
                            navController.navigate(AppScreen.BibleBook.createRoute(index))
                        },
                        bibleViewModel,
                        innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState.value = it },
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
                    val bookIndex =
                        backStackEntry.arguments?.getString(AppScreen.BibleBook.ARG_BOOK_INDEX)?.toIntOrNull()
                            ?: 0
                    BibleBookScreen(
                        { bookIndex, chapterIndex ->
                            navController.navigate(AppScreen.BibleChapter.createRoute(bookIndex, chapterIndex))
                        },
                        bibleViewModel,
                        bookIndex,
                        innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState.value = it },
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
                        AppScreen.BibleChapter.DEEP_LINK_PATTERN.let { listOf(navDeepLink { uriPattern = it }) },
                ) { backStackEntry ->
                    val bookIndex =
                        backStackEntry.arguments
                            ?.getString(AppScreen.BibleChapter.ARG_BOOK_INDEX)
                            ?.toIntOrNull() ?: 0
                    val chapterIndex =
                        backStackEntry.arguments
                            ?.getString(AppScreen.BibleChapter.ARG_CHAPTER_INDEX)
                            ?.toIntOrNull() ?: 0
                    BibleChapterScreen(
                        bibleViewModel,
                        bookIndex,
                        chapterIndex,
                        innerPadding,
                        { bookIndex, chapterIndex ->
                            AppScreen.BibleChapter.createDeepLink(bookIndex, chapterIndex)
                        },
                        routeFactory = {
                            AppScreen.BibleChapter.createRoute(it.bookIndex, it.chapterIndex)
                        },
                    ) { scaffoldUiState.value = it }
                }

                composable(
                    AppScreen.Calendar.route,
                    deepLinks = AppScreen.Calendar.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
                ) {
                    CalendarLiturgicalSeasonScreen(
                        calendarViewModel,
                        contentPadding = innerPadding,
                        onBibleNavigate = {
                            navController.navigate(AppScreen.BibleReader.route)
                        },
                        onPrayerNavigate = { route ->
                            navController.navigate(AppScreen.Prayer.createRoute(route))
                        },
                        onScaffoldStateChanged = { scaffoldUiState.value = it },
                    )
                }

                composable(AppScreen.BibleReader.route) {
                    BibleReadingScreen(
                        calendarViewModel,
                        innerPadding,
                    ) { scaffoldUiState.value = it }
                }

                composable(AppScreen.QrScanner.route) {
                    QrScannerView(
                        onNavigate = { route ->
                            analyticsService.logEvent(AnalyticsEvent.QrNavigationSuccess(route))
                            navController.navigate(route) {
                                launchSingleTop = true
                                navController.popBackStack(AppScreen.QrScanner.route, inclusive = true)
                            }
                        },
                        contentPadding = innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState.value = it },
                    )
                }

                composable(
                    AppScreen.Settings.route,
                    deepLinks = AppScreen.Settings.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
                ) {
                    SettingsScreen(
                        onNavigateToAbout = { navController.navigate(AppScreen.About.route) },
                        requestDndPermission = {
                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            if (!notificationManager.isNotificationPolicyAccessGranted) {
                                Toast
                                    .makeText(
                                        context,
                                        "Please grant the app access to modify DND in settings.",
                                        Toast.LENGTH_LONG,
                                    ).show()
                                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                                context.startActivity(intent)
                            }
                        },
                        settingsViewModel = settingsViewModel,
                        shareService = shareService,
                        showSoundModeSetting = settingsViewModel.showSoundModeSetting,
                        contentPadding = innerPadding,
                    ) { scaffoldUiState.value = it }
                }

                composable(
                    AppScreen.About.route,
                    deepLinks = AppScreen.About.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
                ) {
                    AboutScreen(
                        innerPadding,
                        settingsViewModel.versionName,
                        {
                            val intent =
                                Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:".toUri()
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("samuel.alex.koshy@gmail.com"))
                                    putExtra(Intent.EXTRA_SUBJECT, "Malankara Orthodox Liturgica App Feedback")
                                }
                            try {
                                context.startActivity(Intent.createChooser(intent, "Send Email"))
                            } catch (_: ActivityNotFoundException) {
                                Toast.makeText(context, "No email apps installed", Toast.LENGTH_SHORT).show()
                            }
                        },
                        {
                            val intent = Intent(Intent.ACTION_VIEW, it.toUri())
                            context.startActivity(intent)
                        },
                    ) { scaffoldUiState.value = it }
                }
            }
        }
    }

    if (isDrawerMode) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.primary,
                    drawerContentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.width(240.dp),
                ) {
                    Spacer(Modifier.height(16.dp))
                    navItems.filter { !it.isRailOnly }.forEach { item ->
                        NavigationDrawerItem(
                            icon = item.icon,
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    navController.popBackStack(item.route, inclusive = true)
                                }
                            },
                            colors = drawerItemColors,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    navItems.filter { it.isRailOnly }.forEach { item ->
                        NavigationDrawerItem(
                            icon = item.icon,
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    navController.popBackStack(item.route, inclusive = true)
                                }
                            },
                            colors = drawerItemColors,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        )
                    }
                }
            },
        ) {
            AppScaffold()
        }
    } else {
        Row(Modifier.fillMaxSize()) {
            if (isRailMode) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Spacer(Modifier.height(16.dp))
                    navItems.filter { !it.isRailOnly }.forEach { item ->
                        NavigationRailItem(
                            icon = item.icon,
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    navController.popBackStack(item.route, inclusive = true)
                                }
                            },
                            colors = railItemColors,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    navItems.filter { it.isRailOnly }.forEach { item ->
                        NavigationRailItem(
                            icon = item.icon,
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    navController.popBackStack(item.route, inclusive = true)
                                }
                            },
                            colors = railItemColors,
                        )
                    }
                }
            }
            AppScaffold()
        }
    }
}

private fun Bundle.readNavArgAsString(
    key: String,
    navType: NavType<*>?,
): String? =
    navType
        ?.let { type ->
            runCatching { type[this, key]?.toString() }.getOrNull()
        } ?: get(key)?.toString()

@Composable
private fun ContentLoadingScreen(
    contentPadding: PaddingValues,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
) {
    LaunchedEffect(Unit) {
        onScaffoldStateChanged(ScaffoldUiState.None)
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
