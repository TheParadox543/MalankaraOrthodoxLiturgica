package com.paradox543.malankaraorthodoxliturgica.core.ui.navigation

import androidx.compose.runtime.Composable

data class NavigationItem(
    val route: String,
    val label: String,
    val isRailOnly: Boolean = false,
    val icon: @Composable () -> Unit,
)