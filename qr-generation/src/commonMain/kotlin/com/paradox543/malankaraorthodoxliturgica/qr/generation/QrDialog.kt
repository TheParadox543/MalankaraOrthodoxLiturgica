package com.paradox543.malankaraorthodoxliturgica.qr.generation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun QrDialog(
    imageBitmap: ImageBitmap?,
    onDismissRequest: () -> Unit,
) {
    imageBitmap?.let {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("QR Code") },
            text = {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    modifier = Modifier.size(250.dp),
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