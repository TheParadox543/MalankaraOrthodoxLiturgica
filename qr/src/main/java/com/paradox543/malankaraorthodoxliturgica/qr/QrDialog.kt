package com.paradox543.malankaraorthodoxliturgica.qr

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun QrDialog(
    qrBitmap: Bitmap?,
    onDismissRequest: () -> Unit,
) {
    qrBitmap?.let {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("QR Code") },
            text = {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.Companion.size(250.dp),
                )
            },
            confirmButton = {
                Button(onClick = onDismissRequest) {
                    Text("Close")
                }
            },
        )
    }
}