package com.paradox543.malankaraorthodoxliturgica.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.paradox543.malankaraorthodoxliturgica.core.ui.navigation.bottomNavItems

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavItemClick: (String) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = item.icon,
                label = { Text(item.label, textAlign = TextAlign.Center) },
                selected = currentRoute == item.route,
                onClick = { onNavItemClick(item.route) },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        }
    }
}