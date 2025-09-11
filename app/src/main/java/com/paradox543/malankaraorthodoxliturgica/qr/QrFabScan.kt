package com.paradox543.malankaraorthodoxliturgica.qr

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen

@Composable
fun QrFabScan(navController: NavController) {
    FloatingActionButton(onClick = {
        navController.navigate(Screen.QrScanner.route)
    }) {
        Icon(
            painterResource(R.drawable.qr_scanner),
            contentDescription = "Scan QR"
        )
    }
}