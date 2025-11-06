package com.paradox543.malankaraorthodoxliturgica.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen
import com.paradox543.malankaraorthodoxliturgica.qr.QrScannerView
import com.paradox543.malankaraorthodoxliturgica.view.AboutScreen
import com.paradox543.malankaraorthodoxliturgica.view.BibleBookScreen
import com.paradox543.malankaraorthodoxliturgica.view.BibleChapterScreen
import com.paradox543.malankaraorthodoxliturgica.view.BibleReadingScreen
import com.paradox543.malankaraorthodoxliturgica.view.BibleScreen
import com.paradox543.malankaraorthodoxliturgica.view.CalendarScreen
import com.paradox543.malankaraorthodoxliturgica.view.ContentNotReadyScreen
import com.paradox543.malankaraorthodoxliturgica.view.HomeScreen
import com.paradox543.malankaraorthodoxliturgica.view.OnboardingScreen
import com.paradox543.malankaraorthodoxliturgica.view.PrayNowScreen
import com.paradox543.malankaraorthodoxliturgica.view.PrayerScreen
import com.paradox543.malankaraorthodoxliturgica.view.SectionScreen
import com.paradox543.malankaraorthodoxliturgica.view.SettingsScreen
import com.paradox543.malankaraorthodoxliturgica.view.SongScreen
import com.paradox543.malankaraorthodoxliturgica.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.CalendarViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    settingsViewModel: SettingsViewModel,
    navViewModel: NavViewModel,
    modifier: Modifier = Modifier,
) {
    val prayerViewModel: PrayerViewModel = hiltViewModel()
    val bibleViewModel: BibleViewModel = hiltViewModel()
    val navController = rememberNavController()
    val onboardingStatus by settingsViewModel.hasCompletedOnboarding.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val arguments = navBackStackEntry?.arguments
    val rootNode by navViewModel.rootNode.collectAsState()
    LaunchedEffect(currentRoute, arguments) {
        if (currentRoute != null) {
            settingsViewModel.logScreensVisited(currentRoute, arguments)
        }
    }

    NavHost(
        navController,
        startDestination = if (onboardingStatus) {
            Screen.Home.route
        } else {
            Screen.Onboarding.route
        }
    ) {
        composable(
            Screen.Home.route,
            deepLinks = listOf(navDeepLink { uriPattern = Screen.Home.deepLink!! } )
        ) {
            HomeScreen(navController, prayerViewModel, settingsViewModel, navViewModel)
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController, settingsViewModel, prayerViewModel)
        }

        composable(
            route = Screen.Section.route,
            arguments = listOf(navArgument(Screen.Section.ARG_ROUTE) { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = Screen.Section.DEEP_LINK_PATTERN })
        ) { backStackEntry ->
            val route = backStackEntry.arguments?.getString(Screen.Section.ARG_ROUTE) ?: ""
            val node = navViewModel.findNode(rootNode, route)
            if (node != null) {
                SectionScreen(navController, prayerViewModel, settingsViewModel, node)
            } else {
                ContentNotReadyScreen(navController, modifier, message = route)
            }
        }

        composable(
            route = Screen.Prayer.route,
            arguments = listOf(navArgument(Screen.Prayer.ARG_ROUTE) { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = Screen.Prayer.DEEP_LINK_PATTERN })
        ) { backStackEntry ->
            val prayerRoute = backStackEntry.arguments?.getString(Screen.Prayer.ARG_ROUTE) ?: ""
            val scrollIndex = backStackEntry.arguments?.getString(Screen.Prayer.ARG_SCROLL)?.toIntOrNull() ?: 0
            val node = navViewModel.findNode(rootNode, prayerRoute)
            if (node != null) {
                PrayerScreen(
                    navController,
                    prayerViewModel,
                    settingsViewModel,
                    navViewModel,
                    node,
                    scrollIndex
                )
            } else {
                ContentNotReadyScreen(navController, message = prayerRoute)
            }
        }

        composable(
            route = Screen.Song.route,
            arguments = listOf(navArgument(Screen.Song.ARG_ROUTE) { type = NavType.StringType }),
        ) { backStackEntry ->
            val route = backStackEntry.arguments?.getString(Screen.Section.ARG_ROUTE) ?: ""
            val node = navViewModel.findNode(rootNode, route)
            if (node != null) {
                SongScreen(navController, songFilename = node.filename ?: "")
            } else {
                ContentNotReadyScreen(navController, message = route)
            }
        }

        composable(Screen.PrayNow.route) {
            PrayNowScreen(navController, settingsViewModel, prayerViewModel, navViewModel)
        }

        composable(
            Screen.Bible.route,
            deepLinks = listOf(navDeepLink { uriPattern = Screen.Bible.deepLink!! })
        ) {
            BibleScreen(navController, settingsViewModel, bibleViewModel)
        }

        composable(
            route = Screen.BibleBook.route,
            arguments = listOf(navArgument(Screen.BibleBook.ARG_BOOK) { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = Screen.BibleBook.DEEP_LINK_PATTERN })
        ) { backStackEntry ->
            val book = backStackEntry.arguments?.getString(Screen.BibleBook.ARG_BOOK) ?: ""
            BibleBookScreen(navController, settingsViewModel, bibleViewModel, book)
        }

        composable(
            route = Screen.BibleChapter.route,
            arguments = listOf(
                navArgument(Screen.BibleChapter.ARG_BOOK_INDEX) { type = NavType.StringType }
            ),
            deepLinks = listOf(navDeepLink { uriPattern = Screen.BibleChapter.DEEP_LINK_PATTERN })
        ) { backStackEntry ->
            val bookIndex = backStackEntry.arguments?.getString(Screen.BibleChapter.ARG_BOOK_INDEX)?.toIntOrNull() ?: 0
            val chapterIndex = backStackEntry.arguments?.getString(Screen.BibleChapter.ARG_CHAPTER_INDEX)?.toIntOrNull() ?: 0
            BibleChapterScreen(navController, settingsViewModel, bibleViewModel, bookIndex, chapterIndex)
        }

        composable(Screen.BibleReader.route) {
            BibleReadingScreen(navController, bibleViewModel, settingsViewModel)
        }

        composable(
            Screen.Calendar.route,
            deepLinks = listOf(navDeepLink { uriPattern = Screen.Calendar.deepLink!! })
        ) {
            CalendarScreen(navController, bibleViewModel)
        }

        composable(
            Screen.QrScanner.route
        ) {
            QrScannerView(navController)
        }

        composable(
            Screen.Settings.route,
            deepLinks = listOf(navDeepLink { uriPattern = Screen.Settings.deepLink!! })
        ) {
            SettingsScreen(navController, settingsViewModel)
        }

        composable(
            Screen.About.route,
            deepLinks = listOf(navDeepLink { uriPattern = Screen.About.deepLink!! })
        ) {
            AboutScreen(navController)
        }
    }
}
