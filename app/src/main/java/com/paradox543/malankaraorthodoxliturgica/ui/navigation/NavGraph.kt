package com.paradox543.malankaraorthodoxliturgica.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.paradox543.malankaraorthodoxliturgica.qr.QrScannerView
import com.paradox543.malankaraorthodoxliturgica.services.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.services.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.services.ShareService
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
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    inAppReviewManager: InAppReviewManager,
    analyticsService: AnalyticsService,
    shareService: ShareService,
    modifier: Modifier = Modifier.Companion,
) {
    val prayerViewModel: PrayerViewModel = hiltViewModel()
    val bibleViewModel: BibleViewModel = hiltViewModel()
    val prayerNavViewModel: PrayerNavViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val navController = rememberNavController()
    val onboardingStatus by settingsViewModel.onboardingCompleted.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val arguments = navBackStackEntry?.arguments
    navController.addOnDestinationChangedListener { _, destination, arguments ->
        analyticsService.logScreensVisited(destination.route ?: "", arguments)
    }

    NavHost(
        navController,
        startDestination =
            if (onboardingStatus) {
                AppScreen.Home.route
            } else {
                AppScreen.Onboarding.route
            },
    ) {
        composable(
            AppScreen.Home.route,
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.Home.deepLink!! }),
        ) {
            HomeScreen(navController, prayerViewModel, prayerNavViewModel, inAppReviewManager)
        }

        composable(AppScreen.Onboarding.route) {
            OnboardingScreen(navController, settingsViewModel, prayerViewModel)
        }

        composable(
            route = AppScreen.Section.route,
            arguments =
                listOf(
                    navArgument(AppScreen.Section.ARG_ROUTE) {
                        type = NavType.Companion.StringType
                    },
                ),
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.Section.DEEP_LINK_PATTERN }),
        ) { backStackEntry ->
            val route = backStackEntry.arguments?.getString(AppScreen.Section.ARG_ROUTE) ?: ""
            val node = prayerNavViewModel.findNode(route)
            if (node != null) {
                SectionScreen(navController, prayerViewModel, node, inAppReviewManager)
            } else {
                ContentNotReadyScreen(navController, modifier, message = route)
            }
        }

        composable(
            route = AppScreen.Prayer.route,
            arguments =
                listOf(
                    navArgument(AppScreen.Prayer.ARG_ROUTE) {
                        type = NavType.Companion.StringType
                    },
                ),
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.Prayer.DEEP_LINK_PATTERN }),
        ) { backStackEntry ->
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
                )
            } else {
                ContentNotReadyScreen(navController, message = prayerRoute)
            }
        }

        composable(
            route = AppScreen.Song.route,
            arguments =
                listOf(
                    navArgument(AppScreen.Song.ARG_ROUTE) {
                        type = NavType.Companion.StringType
                    },
                ),
        ) { backStackEntry ->
            val route = backStackEntry.arguments?.getString(AppScreen.Section.ARG_ROUTE) ?: ""
            val node = prayerNavViewModel.findNode(route)
            if (node != null) {
                SongScreen(navController, songFilename = node.filename ?: "")
            } else {
                ContentNotReadyScreen(navController, message = route)
            }
        }

        composable(AppScreen.PrayNow.route) {
            PrayNowScreen(navController, settingsViewModel, prayerViewModel, prayerNavViewModel)
        }

        composable(
            AppScreen.Bible.route,
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.Bible.deepLink!! }),
        ) {
            BibleScreen(navController, settingsViewModel, bibleViewModel)
        }

        composable(
            route = AppScreen.BibleBook.route,
            arguments =
                listOf(
                    navArgument(AppScreen.BibleBook.ARG_BOOK_INDEX) {
                        type = NavType.Companion.StringType
                    },
                ),
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.BibleBook.DEEP_LINK_PATTERN }),
        ) { backStackEntry ->
            val bookIndex =
                backStackEntry.arguments?.getString(AppScreen.BibleBook.ARG_BOOK_INDEX)?.toIntOrNull()
                    ?: 0
            BibleBookScreen(navController, settingsViewModel, bibleViewModel, bookIndex)
        }

        composable(
            route = AppScreen.BibleChapter.route,
            arguments =
                listOf(
                    navArgument(AppScreen.BibleChapter.ARG_BOOK_INDEX) {
                        type = NavType.Companion.StringType
                    },
                ),
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.BibleChapter.DEEP_LINK_PATTERN }),
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
                navController,
                settingsViewModel,
                bibleViewModel,
                bookIndex,
                chapterIndex,
            )
        }

        composable(AppScreen.BibleReader.route) {
            BibleReadingScreen(navController, bibleViewModel, settingsViewModel)
        }

        composable(
            AppScreen.Calendar.route,
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.Calendar.deepLink!! }),
        ) {
            CalendarScreen(navController, bibleViewModel)
        }

        composable(
            AppScreen.QrScanner.route,
        ) {
            QrScannerView(navController)
        }

        composable(
            AppScreen.Settings.route,
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.Settings.deepLink!! }),
        ) {
            SettingsScreen(navController, settingsViewModel, shareService)
        }

        composable(
            AppScreen.About.route,
            deepLinks = listOf(navDeepLink { uriPattern = AppScreen.About.deepLink!! }),
        ) {
            AboutScreen(navController)
        }
    }
}