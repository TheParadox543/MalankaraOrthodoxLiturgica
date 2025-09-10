package com.paradox543.malankaraorthodoxliturgica.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun QrFab(
    navController: NavController,
    currentDeepLink: String
) {
    var showDialog by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    FloatingActionButton(onClick = {
        qrBitmap = generateQrBitmap(currentDeepLink)
        showDialog = true
    }) {
        Icon(Icons.Filled.Add, contentDescription = "Generate QR")
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
            }
        )
    }
}