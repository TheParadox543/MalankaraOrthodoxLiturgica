package com.paradox543.malankaraorthodoxliturgica.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrFabGenerate(
    routeProvider: () -> String, // Pass the onClick String from parent
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val tooltipState = rememberTooltipState()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            Card {
                Text(
                    "Generate QR Code",
                    Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        state = tooltipState,
    ) {
        FloatingActionButton(
            onClick = {
                qrBitmap = generateQrBitmap(routeProvider())
                showDialog = true
            },
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(painterResource(R.drawable.qr_code), contentDescription = "Generate QR")
        }
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