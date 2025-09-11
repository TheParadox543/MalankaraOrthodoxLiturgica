package com.paradox543.malankaraorthodoxliturgica.qr

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
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.data.model.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrFabScan(
    navController: NavController,
    modifier: Modifier = Modifier,
    primary: Boolean = true
) {
    val tooltipState = rememberTooltipState()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            Card {
                Text(
                    "Scan QR Code",
                    Modifier.padding(12.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        state = tooltipState,
    ) {
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.QrScanner.route)
            },
            modifier = modifier,
            containerColor = if (primary) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            },
            contentColor = if (primary) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSecondary
            }
        ) {
            Icon(
                painterResource(R.drawable.qr_scanner),
                contentDescription = "Scan QR"
            )
        }
    }
}