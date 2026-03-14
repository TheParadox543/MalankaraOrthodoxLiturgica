package com.paradox543.malankaraorthodoxliturgica.core.ui.components

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.paradox543.malankaraorthodoxliturgica.core.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrFabScan(
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    val tooltipState = rememberTooltipState()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            Card {
                Text(
                    "Scan QR Code",
                    Modifier.Companion.padding(12.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        state = tooltipState,
    ) {
        FloatingActionButton(
            onClick = onScanClick,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(
                painterResource(R.drawable.qr_scanner),
                contentDescription = "Scan QR",
            )
        }
    }
}