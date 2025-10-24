package com.paradox543.malankaraorthodoxliturgica.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.qr.generateQrBitmap

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit,
)

val iconSize = 24.dp
val bottomNavItems =
    listOf(
        BottomNavItem("home", "Home") {
            Icon(Icons.Default.Home, "Home")
        },
        BottomNavItem("prayNow", "Pray Now") {
            Icon(
                painterResource(R.drawable.clock),
                "Clock",
                modifier = Modifier.size(iconSize),
            )
        },
        BottomNavItem("calendar", "Calendar") {
            Icon(
                painterResource(R.drawable.calendar),
                "Calendar",
                Modifier.size(iconSize),
            )
        },
        BottomNavItem("bible", "Bible") {
            Icon(
                painterResource(R.drawable.bible),
                "Bible",
                modifier = Modifier.size(iconSize),
            )
        },
    )

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute =
        navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon =  item.icon,
                label = { Text(item.label, textAlign = TextAlign.Center) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.popBackStack(item.route, inclusive = true)
                    }
                },
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

@Composable
fun SectionNavBar(
    navController: NavController,
    prevNodeRoute: String?,
    nextNodeRoute: String?,
    routeProvider: () -> String,
) {
    var showDialog by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

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
            onClick = {
                navController.navigate(prevNodeRoute!!) {
                    navController.popBackStack()
                }
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
            onClick = {
                navController.navigate(nextNodeRoute!!) {
                    navController.popBackStack()
                }
            },
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
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("QR Code") },
            text = {
                Image(bitmap = qrBitmap!!.asImageBitmap(), contentDescription = null, modifier = Modifier.size(250.dp))
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Close")
                }
            },
        )
    }
}