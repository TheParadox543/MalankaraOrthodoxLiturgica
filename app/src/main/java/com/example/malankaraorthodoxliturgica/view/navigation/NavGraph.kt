package com.example.malankaraorthodoxliturgica.view.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.malankaraorthodoxliturgica.PrayerDetailScreen
import com.example.malankaraorthodoxliturgica.view.GreatLentDayScreen
import com.example.malankaraorthodoxliturgica.view.GreatLentPrayerScreen
import com.example.malankaraorthodoxliturgica.view.GreatLentScreen
import com.example.malankaraorthodoxliturgica.view.HomeScreen
import com.example.malankaraorthodoxliturgica.view.PrayerListScreen
import com.example.malankaraorthodoxliturgica.view.QurbanaScreen
import com.example.malankaraorthodoxliturgica.view.SettingsScreen
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Default.Home, "Home"),
    BottomNavItem("settings", Icons.Default.Settings, "Settings")
)

@Composable
fun BottomNavBar(navController: NavController, prayerViewModel: PrayerViewModel) {
    val sectionNavigation by prayerViewModel.sectionNavigation.collectAsState()

    if (sectionNavigation) {
        // Sequential Navigation (Previous/Next)
        SequentialNavBar(navController, prayerViewModel)
    } else {
        // Original Bottom Navigation
        DefaultBottomNavBar(navController)
    }
}

@Composable
fun DefaultBottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}

@Composable
fun SequentialNavBar(navController: NavController, prayerViewModel: PrayerViewModel) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    Log.d("Sequential NavBar", currentRoute ?: "Can't display")

    val prayerOrder = prayerViewModel.getDayPrayers()
    val baseRoute = currentRoute?.substringBeforeLast("_") ?: ""
    val currentIndex = currentRoute?.substringAfterLast("_")?.toIntOrNull() ?: -1

    val previousRoute = if (currentIndex > 0) "${baseRoute}_${currentIndex - 1}" else null
    val nextRoute = if (currentIndex in 0 until prayerOrder.lastIndex) "${baseRoute}_${currentIndex + 1}" else null

    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = if (previousRoute != null) Color.Black else Color.Gray
                )
            },
            label = { Text("Previous") },
            selected = false,
            enabled = previousRoute != null,
            onClick = { previousRoute?.let { navController.navigate(it) } }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = if (nextRoute != null) Color.Black else Color.Gray
                )
            },
            label = { Text("Next") },
            selected = false,
            enabled = nextRoute != null,
            onClick = { nextRoute?.let { navController.navigate(it) } }
        )
    }
}



@Composable
fun NavGraph(prayerViewModel: PrayerViewModel, modifier: Modifier) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController, prayerViewModel) }
    ) { padding ->
        NavHost(navController, startDestination = "home", Modifier.padding(padding)) {
            composable("home") {
                prayerViewModel.setSectionNavigation(false)
                HomeScreen(navController, prayerViewModel)
            }
            composable("prayer_list/{category}"){navBackStackEntry ->
                val category = navBackStackEntry.arguments?.getString("category")?:""
                prayerViewModel.setSectionNavigation(false)
                PrayerListScreen(navController, prayerViewModel, category)
            }
            composable("prayer_detail/{category}"){navBackStackEntry ->
                val category = navBackStackEntry.arguments?.getString("category")?:""
                prayerViewModel.setSectionNavigation(false)
                PrayerDetailScreen(navController, LocalContext.current, category, "en")
            }
            composable("great_lent_main"){
                prayerViewModel.setSectionNavigation(false)
                GreatLentScreen(navController, prayerViewModel)
            }
            composable("great_lent_day/{day}"){ navBackStackEntry ->
                val day = navBackStackEntry.arguments?.getString("day")?:""
                prayerViewModel.setSectionNavigation(false)
                GreatLentDayScreen(navController, prayerViewModel, day)
            }
            composable("great_lent_prayer/{day}/{time}"){navBackStackEntry ->
                val day = navBackStackEntry.arguments?.getString("day")?:""
                val time = navBackStackEntry.arguments?.getString("time")?.toIntOrNull()?: 0
                prayerViewModel.setSectionNavigation(true)
                GreatLentPrayerScreen(navController, prayerViewModel, day, time)
            }
            composable("qurbana"){
                prayerViewModel.setSectionNavigation(false)
                QurbanaScreen(navController, prayerViewModel)
            }
            composable("settings") {
                prayerViewModel.setSectionNavigation(false)
                SettingsScreen(navController, prayerViewModel)
            }
        }
    }
}
