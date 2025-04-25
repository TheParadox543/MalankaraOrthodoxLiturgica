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
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel


@Composable
fun BottomNavBar(navController: NavController, prayerViewModel: PrayerViewModel, navViewModel: NavViewModel) {
    val sectionNavigation by prayerViewModel.sectionNavigation.collectAsState()
    val isVisible = rememberScrollAwareVisibility() // Track scroll visibility

    if (sectionNavigation) {
        SeqNavBar(navController, prayerViewModel, navViewModel)
    } else {
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
                onClick = {
                    if (currentRoute == "settings"){
                        navController.navigateUp()
                    } else {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}

@Composable
fun SeqNavBar(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel
) {
    val topBarNames by prayerViewModel.topBarNames.collectAsState()
    val currentNode = navViewModel.currentNode
    val hasPrev by navViewModel.hasPrevSibling.collectAsState()
    val hasNext by navViewModel.hasNextSibling.collectAsState()

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
            enabled = hasPrev,
            onClick = {
                val prevFileName = navViewModel.goToPrevSibling()
                prayerViewModel.setFilename(prevFileName)
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
            enabled = hasNext,
            onClick = {
                val nextFilename = navViewModel.goToNextSibling()
                prayerViewModel.setFilename(nextFilename)
            }
        )
    }
}