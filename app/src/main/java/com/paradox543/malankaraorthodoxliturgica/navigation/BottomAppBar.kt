package com.paradox543.malankaraorthodoxliturgica.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.paradox543.malankaraorthodoxliturgica.R

data class BottomNavItem(val route: String, val label: String, val icon: @Composable () -> Unit)

val iconSize = 24.dp
val bottomNavItems = listOf(
    BottomNavItem(
        "home", "Home"
    ) {
        Icon(Icons.Default.Home, "Home")
    },
    BottomNavItem(
        "prayNow", "Pray Now"
    ) {
        Icon(
            painterResource(R.drawable.clock),
            "Clock",
            modifier = Modifier.size(iconSize),
        )
    },
    BottomNavItem(
        "bible", "Bible"
    ) {
        Icon(
            painterResource(R.drawable.bible),
            "Bible",
            modifier = Modifier.size(iconSize),
        )
    },
    BottomNavItem(
        "settings", "Settings"
    ) {
        Icon(Icons.Default.Settings, "Settings")
    }
)

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon =  item.icon,
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.popBackStack(item.route, inclusive = true)
                    }
                }
            )
        }
    }
}

@Composable
fun SectionNavBar(
    navController: NavController,
    prevNodeRoute: String?,
    nextNodeRoute: String?
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                )
            },
            label = { Text("Previous") },
            selected = false,
            enabled = prevNodeRoute != null,
            onClick = {
                navController.navigate(prevNodeRoute!!) {
                    navController.popBackStack()
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                )
            },
            label = { Text("Next") },
            selected = false,
            enabled = nextNodeRoute != null,
            onClick = {
                navController.navigate(nextNodeRoute!!) {
                    navController.popBackStack()
                }
            }
        )
    }
}