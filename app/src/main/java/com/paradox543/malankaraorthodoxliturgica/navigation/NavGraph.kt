package com.paradox543.malankaraorthodoxliturgica.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import com.paradox543.malankaraorthodoxliturgica.viewmodel.BibleViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel,
    navViewModel: NavViewModel,
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
            "home"
        } else {
            "onboarding"
        }
    ) {
        composable("home") {
            HomeScreen(navController, prayerViewModel, settingsViewModel, navViewModel)
        }
        composable("onboarding") {
            OnboardingScreen(navController, settingsViewModel, prayerViewModel)
        }
        composable("section/{route}") { backStackEntry ->
            val route = backStackEntry.arguments?.getString("route") ?: ""
            val node = navViewModel.findNode(rootNode, route)
            if (node != null) {
                SectionScreen(navController, prayerViewModel, settingsViewModel, node)
            } else {
                ContentNotReadyScreen(navController = navController, message = route)
            }
        }
        composable("prayerScreen/{route}") { backStackEntry ->
            val route = backStackEntry.arguments?.getString("route") ?: ""
            val node = navViewModel.findNode(rootNode, route)
            if (node != null) {
                PrayerScreen(navController, prayerViewModel, settingsViewModel, navViewModel, node)
            } else {
                ContentNotReadyScreen(navController = navController, message = route)
            }
        }
        composable("prayNow") {
            PrayNowScreen(navController, settingsViewModel, prayerViewModel, navViewModel)
        }
        composable("bible") {
            BibleScreen(navController, settingsViewModel, bibleViewModel)
        }
        composable("bible/{bookName}") {backStackEntry ->
            val book = backStackEntry.arguments?.getString("bookName") ?: ""
            BibleBookScreen(navController, settingsViewModel, bibleViewModel, book)
        }
        composable("bible/{bookIndex}/{chapterIndex}") {backStackEntry ->
            val bookIndex = backStackEntry.arguments?.getString("bookIndex") ?: ""
            val chapterIndex = backStackEntry.arguments?.getString("chapterIndex") ?: ""
            BibleChapterScreen(
                navController,
                settingsViewModel,
                bibleViewModel,
                bookIndex.toInt(),
                chapterIndex.toInt()
            )
        }
        composable("bibleReaderScreen") {
            BibleReadingScreen(navController, bibleViewModel, settingsViewModel)
        }
        composable("calendar") {
            CalendarScreen(navController, bibleViewModel)
        }
        composable("settings") {
            SettingsScreen(navController, settingsViewModel)
        }
    }
}
