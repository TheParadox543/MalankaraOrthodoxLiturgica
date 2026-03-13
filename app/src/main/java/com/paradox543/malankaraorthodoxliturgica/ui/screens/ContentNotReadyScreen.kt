package com.paradox543.malankaraorthodoxliturgica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.ui.ScaffoldUiState

@Composable
fun ContentNotReadyScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    message: String? = null,
    contentPadding: PaddingValues = PaddingValues(),
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit = {},
) {
    LaunchedEffect(Unit) { onScaffoldStateChanged(ScaffoldUiState.Standard("Error", showBottomBar = false)) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Content for this page is not yet ready.".replace("this page", message ?: "this page"),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        Text(
            text = "Please check back later for updates!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Button(onClick = { navController.navigateUp() }) {
            Text("Go Back")
        }
    }
}