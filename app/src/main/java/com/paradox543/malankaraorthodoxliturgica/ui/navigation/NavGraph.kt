package com.paradox543.malankaraorthodoxliturgica.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.qr.QrScannerView
import com.paradox543.malankaraorthodoxliturgica.ui.ScaffoldUiState
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
 * NavGraph wiring for the app. Each destination obtains its Hilt-backed ViewModels inside
 * the composable lambda so that they are scoped to the destination's NavBackStackEntry.
 *
 * The [navController] is hoisted from MainActivity so the single Scaffold's bars can use it.
 * [contentPadding] is the innerPadding from MainActivity's Scaffold, forwarded to each screen.
 * [onScaffoldStateChanged] lets each screen declare which bars/FAB it needs.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    onboardingCompleted: Boolean,
    inAppReviewManager: InAppReviewManager,
    analyticsService: AnalyticsService,
    shareService: ShareService,
    settingsViewModel: SettingsViewModel,
    contentPadding: PaddingValues,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
) {
    val bibleViewModel: BibleViewModel = hiltViewModel()

    // Add and remove the destination change listener cleanly to avoid leaks
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
                contentPadding,
                onScaffoldStateChanged,
            )
        }

        composable(AppScreen.Onboarding.route) {
            val prayerViewModel: PrayerViewModel = hiltViewModel()
            OnboardingScreen(navController, settingsViewModel, prayerViewModel, contentPadding, onScaffoldStateChanged)
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
                SectionScreen(navController, prayerViewModel, node, inAppReviewManager, contentPadding, onScaffoldStateChanged)
            } else {
                ContentNotReadyScreen(
                    navController,
                    message = route,
                    contentPadding = contentPadding,
                    onScaffoldStateChanged = onScaffoldStateChanged,
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
                    navController,
                    prayerViewModel,
                    settingsViewModel,
                    prayerNavViewModel,
                    node,
                    scrollIndex,
                    contentPadding,
                    onScaffoldStateChanged,
                )
            } else {
                ContentNotReadyScreen(
                    navController,
                    message = prayerRoute,
                    contentPadding = contentPadding,
                    onScaffoldStateChanged = onScaffoldStateChanged,
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
                    navController,
                    songFilename = node.filename ?: "",
                    contentPadding = contentPadding,
                    onScaffoldStateChanged = onScaffoldStateChanged,
                )
            } else {
                ContentNotReadyScreen(
                    navController,
                    message = route,
                    contentPadding = contentPadding,
                    onScaffoldStateChanged = onScaffoldStateChanged,
                )
            }
        }

        composable(AppScreen.PrayNow.route) { backStackEntry ->
            val prayerViewModel = hiltViewModel<PrayerViewModel>(backStackEntry)
            val prayerNavViewModel = hiltViewModel<PrayerNavViewModel>(backStackEntry)
            PrayNowScreen(navController, settingsViewModel, prayerViewModel, prayerNavViewModel, contentPadding, onScaffoldStateChanged)
        }

        composable(
            AppScreen.Bible.route,
            deepLinks = AppScreen.Bible.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
        ) { backStackEntry ->
            val bibleViewModel: BibleViewModel = hiltViewModel(backStackEntry)
            BibleScreen(navController, settingsViewModel, bibleViewModel, contentPadding, onScaffoldStateChanged)
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
            BibleBookScreen(navController, settingsViewModel, bibleViewModel, bookIndex, contentPadding, onScaffoldStateChanged)
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
                navController,
                settingsViewModel,
                bibleViewModel,
                bookIndex,
                chapterIndex,
                contentPadding,
                onScaffoldStateChanged,
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
                contentPadding = contentPadding,
                onScaffoldStateChanged = onScaffoldStateChanged,
            )
        }

        composable(AppScreen.BibleReader.route) { backStackEntry ->
            BibleReadingScreen(navController, bibleViewModel, settingsViewModel, contentPadding, onScaffoldStateChanged)
        }

        composable(AppScreen.QrScanner.route) {
            QrScannerView(navController)
        }

        composable(
            AppScreen.Settings.route,
            deepLinks = AppScreen.Settings.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
        ) {
            SettingsScreen(navController, settingsViewModel, shareService, contentPadding, onScaffoldStateChanged)
        }

        composable(
            AppScreen.About.route,
            deepLinks = AppScreen.About.deepLink?.let { listOf(navDeepLink { uriPattern = it }) } ?: emptyList(),
        ) {
            AboutScreen(navController, contentPadding, onScaffoldStateChanged)
        }
    }
}
