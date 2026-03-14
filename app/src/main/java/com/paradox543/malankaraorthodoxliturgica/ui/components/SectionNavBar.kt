package com.paradox543.malankaraorthodoxliturgica.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.paradox543.malankaraorthodoxliturgica.R

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
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                )
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
                Icon(painterResource(R.drawable.qr_code), contentDescription = "Generate QR")
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
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                )
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
