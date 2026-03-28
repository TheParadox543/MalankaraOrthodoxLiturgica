package com.paradox543.malankaraorthodoxliturgica.ui.navigation

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
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
import com.paradox543.malankaraorthodoxliturgica.MainActivity
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.ui.components.QrFabScan
import com.paradox543.malankaraorthodoxliturgica.core.ui.modifier.globalPinchZoom
import com.paradox543.malankaraorthodoxliturgica.core.ui.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.core.ui.navigation.SectionNavBar
import com.paradox543.malankaraorthodoxliturgica.core.ui.navigation.TopNavBar
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.core.ui.screens.ContentNotReadyScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleBookScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleChapterScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.screens.BibleScreen
import com.paradox543.malankaraorthodoxliturgica.feature.bible.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens.BibleReadingScreen
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens.CalendarScreen
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.screens.OnboardingScreen
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens.HomeScreen
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
    onboardingCompleted: Boolean,
    inAppUpdateManager: InAppUpdateManager,
    analyticsService: AnalyticsService,
    shareService: ShareService,
    settingsViewModel: SettingsViewModel,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val updateDownloaded by inAppUpdateManager.updateDownloaded.collectAsState()
    val context: Context = LocalContext.current

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

    // Create ViewModels once at NavGraph level to prevent recreation glitches
    val prayerViewModel: PrayerViewModel = koinViewModel()
    val prayerNavViewModel: PrayerNavViewModel = koinViewModel()
    val songPlayerViewModel: SongPlayerViewModel = koinViewModel()
    val bibleViewModel: BibleViewModel = koinViewModel()
    val calendarViewModel: CalendarViewModel = koinViewModel()

    // Apply nestedScroll modifier only for PrayerReading state
    val baseScaffoldModifier =
        when (val state = scaffoldUiState) {
            is ScaffoldUiState.PrayerReading -> Modifier.nestedScroll(state.nestedScrollConnection)
            else -> Modifier
        }

    val scaffoldModifier =
        baseScaffoldModifier.globalPinchZoom(
            enabled = pinchZoomEnabled,
            onZoomInStep = { settingsViewModel.setFontScaleDebounced(1) },
            onZoomOutStep = { settingsViewModel.setFontScaleDebounced(-1) },
        )

    Scaffold(
        modifier = scaffoldModifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            when (val state = scaffoldUiState) {
                is ScaffoldUiState.Standard -> {
                    TopNavBar(
                        title = state.title,
                        showBack = currentRoute != AppScreen.Home.route,
                        showSettings = currentRoute != AppScreen.Settings.route,
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
                            showSettings = currentRoute != AppScreen.Settings.route,
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
                            showSettings = currentRoute != AppScreen.Settings.route,
                            onBack = { navController.navigateUp() },
                        ) { navController.navigate(AppScreen.Settings.route) }
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
                HomeScreen(
                    prayerViewModel,
                    prayerNavViewModel,
                    innerPadding,
                    onSectionNavigate = { route ->
                        navController.navigate(AppScreen.Section.createRoute(route))
                    },
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(AppScreen.Onboarding.route) {
                val onboardingViewModel: OnboardingViewModel = koinViewModel()
                OnboardingScreen(
                    onboardingViewModel,
                    innerPadding,
                    {
                        navController.navigate(AppScreen.Home.route) {
                            popUpTo(AppScreen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    },
                ) { scaffoldUiState = it }
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
                val node = prayerNavViewModel.findNode(route)
                if (node != null) {
                    SectionScreen(
                        prayerViewModel,
                        prayerNavViewModel,
                        node,
                        innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState = it },
                        onSectionNavigate = { route ->
                            navController.navigate(AppScreen.Section.createRoute(route))
                        },
                        onPrayerNavigate = { route ->
                            navController.navigate(AppScreen.Prayer.createRoute(route))
                        },
                        onSongNavigate = { route ->
                            navController.navigate(AppScreen.Song.createRoute(route))
                        },
                    )
                } else {
                    ContentNotReadyScreen(
                        message = route,
                        contentPadding = innerPadding,
                        onBackNavigation = { navController.navigateUp() },
                    ) { scaffoldUiState = it }
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
                    ) { scaffoldUiState = it }
                } else {
                    ContentNotReadyScreen(
                        message = prayerRoute,
                        contentPadding = innerPadding,
                        onBackNavigation = { navController.navigateUp() },
                    ) { scaffoldUiState = it }
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
                val node = prayerNavViewModel.findNode(route)
                if (node != null) {
                    SongScreen(
                        songPlayerViewModel = songPlayerViewModel,
                        songFilename = node.filename ?: "",
                        contentPadding = innerPadding,
                        onScaffoldStateChanged = { scaffoldUiState = it },
                    )
                } else {
                    ContentNotReadyScreen(
                        message = route,
                        contentPadding = innerPadding,
                        onBackNavigation = { navController.navigateUp() },
                    ) { scaffoldUiState = it }
                }
            }

            composable(AppScreen.PrayNow.route) { backStackEntry ->
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
            ) {
                BibleScreen(
                    { index ->
                        navController.navigate(AppScreen.BibleBook.createRoute(index))
                    },
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
                ) { scaffoldUiState = it }
            }

            composable(
                AppScreen.Calendar.route,
                deepLinks = AppScreen.Calendar.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
            ) {
                CalendarScreen(
                    calendarViewModel,
                    contentPadding = innerPadding,
                    onBibleNavigate = {
                        navController.navigate(AppScreen.BibleReader.route)
                    },
                    onPrayerNavigate = { route ->
                        navController.navigate(AppScreen.Prayer.createRoute(route))
                    },
                    onScaffoldStateChanged = { scaffoldUiState = it },
                )
            }

            composable(AppScreen.BibleReader.route) {
                BibleReadingScreen(
                    calendarViewModel,
                    innerPadding,
                ) { scaffoldUiState = it }
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
                    onScaffoldStateChanged = { scaffoldUiState = it },
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
                ) { scaffoldUiState = it }
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
                ) { scaffoldUiState = it }
            }
        }
    }
}
