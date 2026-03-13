package com.paradox543.malankaraorthodoxliturgica.ui.navigation

import androidx.compose.runtime.Composable

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit,
)