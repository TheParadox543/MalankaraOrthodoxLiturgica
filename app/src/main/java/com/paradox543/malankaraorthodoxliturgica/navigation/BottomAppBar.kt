package com.paradox543.malankaraorthodoxliturgica.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel

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
    navViewModel: NavViewModel
) {
    val prevSibling by navViewModel.prevSiblingIndex.collectAsState()
    val nextSibling by navViewModel.nextSiblingIndex.collectAsState()

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
            enabled = prevSibling != null,
            onClick = {navViewModel.setCurrentSiblingIndex(prevSibling)}
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
            enabled = nextSibling != null,
            onClick = {navViewModel.setCurrentSiblingIndex(nextSibling)}
        )
    }
}