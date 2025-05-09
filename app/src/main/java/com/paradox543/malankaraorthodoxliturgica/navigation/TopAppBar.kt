package com.paradox543.malankaraorthodoxliturgica.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel,
    onActionClick: (() -> Unit)? = null // Optional Action button
) {
    val topBarNames by prayerViewModel.topBarNames.collectAsState()
    val translations by prayerViewModel.translations.collectAsState()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isVisible = rememberScrollAwareVisibility()

    val title = if (currentRoute == "settings") {
        translations["malankara"] ?: "error"
    } else {
        topBarNames.joinToString(separator = " ") { key ->
            translations[key] ?: "error"
        }
    }

    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Blue,
            titleContentColor = Color.White
        ),
        navigationIcon = {
            if (topBarNames != listOf("malankara") && currentRoute != "settings") {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Page",
                    )
                }
            }
        },
        actions = {
            if (onActionClick != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White
                    )
                }
            }
        }
    )
}
