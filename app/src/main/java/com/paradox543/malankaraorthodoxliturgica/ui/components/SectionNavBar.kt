package com.paradox543.malankaraorthodoxliturgica.ui.components

import android.graphics.Bitmap
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.qr.generateQrBitmap

@Composable
fun SectionNavBar(
    prevNodeRoute: String?,
    nextNodeRoute: String?,
    routeProvider: () -> String,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

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
            onClick = {
                qrBitmap = generateQrBitmap(routeProvider())
                showDialog = true
            },
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
    if (showDialog && qrBitmap != null) {
        QrDialog(qrBitmap, onDismissRequest = { showDialog = false })
    }
}
