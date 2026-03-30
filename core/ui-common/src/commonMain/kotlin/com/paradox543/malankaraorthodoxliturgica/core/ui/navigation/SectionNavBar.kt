package com.paradox543.malankaraorthodoxliturgica.core.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.composables.icons.materialicons.MaterialIcons
import com.composables.icons.materialicons.rounded.Arrow_back
import com.composables.icons.materialicons.rounded.Arrow_forward
import com.composables.icons.materialicons.rounded.Qr_code

@Composable
fun SectionNavBar(
    prevNodeRoute: String?,
    nextNodeRoute: String?,
    onShowQr: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        NavigationBarItem(
            icon = {
                Icon(MaterialIcons.Rounded.Arrow_back, contentDescription = "Previous")
            },
            label = { Text("Previous") },
            selected = false,
            enabled = prevNodeRoute != null,
            onClick = onPrevClick,
            colors =
                NavigationBarItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    disabledIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                    disabledTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                ),
        )
        NavigationBarItem(
            icon = {
                Icon(MaterialIcons.Rounded.Qr_code, contentDescription = "Generate QR")
            },
            label = { Text("Generate QR") },
            selected = false,
            enabled = true,
            onClick = onShowQr,
            colors =
                NavigationBarItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    disabledIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                    disabledTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                ),
        )
        NavigationBarItem(
            icon = {
                Icon(MaterialIcons.Rounded.Arrow_forward, contentDescription = "Next")
            },
            label = { Text("Next") },
            selected = false,
            enabled = nextNodeRoute != null,
            onClick = onNextClick,
            colors =
                NavigationBarItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    disabledIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                    disabledTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                ),
        )
    }
}
