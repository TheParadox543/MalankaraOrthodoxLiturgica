package com.example.malankaraorthodoxliturgica.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.malankaraorthodoxliturgica.PrayerDetailScreen
import com.example.malankaraorthodoxliturgica.SettingsScreen
import com.example.malankaraorthodoxliturgica.view.GreatLentDayScreen
import com.example.malankaraorthodoxliturgica.view.GreatLentPrayerScreen
import com.example.malankaraorthodoxliturgica.view.GreatLentScreen
import com.example.malankaraorthodoxliturgica.view.HomeScreen
import com.example.malankaraorthodoxliturgica.view.PrayerListScreen
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    BottomNavItem("home", Icons.Default.Home, "Home"),
    BottomNavItem("settings", Icons.Default.Settings, "Settings")
)

@Composable
fun BottomNavBar(navController: NavController) {
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
fun NavGraph(prayerViewModel: PrayerViewModel, modifier: Modifier) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        NavHost(navController, startDestination = "home", Modifier.padding(padding)) {
            composable("home") { HomeScreen(navController, prayerViewModel) }
            composable("prayer_list/{category}"){navBackStackEntry ->
                val category = navBackStackEntry.arguments?.getString("category")?:""
                PrayerListScreen(navController, prayerViewModel, category)
            }
            composable("prayer_detail/{category}"){navBackStackEntry ->
                val category = navBackStackEntry.arguments?.getString("category")?:""
                PrayerDetailScreen(navController, LocalContext.current, category, "en")
            }
            composable("great_lent_main"){
                GreatLentScreen(navController, prayerViewModel)
            }
            composable("great_lent_day/{day}"){ navBackStackEntry ->
                val day = navBackStackEntry.arguments?.getString("day")?:""
                GreatLentDayScreen(navController, prayerViewModel, day)
            }
            composable("great_lent_prayer/{day}/{prayer}"){navBackStackEntry ->
                val day = navBackStackEntry.arguments?.getString("day")?:""
                val prayer = navBackStackEntry.arguments?.getString("prayer")?:""
                GreatLentPrayerScreen(navController, prayerViewModel, day, prayer)
            }
            composable("settings") { SettingsScreen(navController) }
        }
    }
}
