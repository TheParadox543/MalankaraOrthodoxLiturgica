package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.navigation.BottomNavBar
import com.paradox543.malankaraorthodoxliturgica.navigation.TopNavBar

@Composable
fun ContentNotReadyScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    message: String? = null,
) {
    Scaffold(
        topBar = { TopNavBar("Error", navController) },
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Content for this page is not yet ready.".replace("this page", message ?: "this page"),
                style = MaterialTheme.typography.headlineSmall, // A prominent style
                color = MaterialTheme.colorScheme.onSurfaceVariant, // A subtle color
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Please check back later for updates!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            // You could add a button to go back, or a refresh button if applicable
             Button(onClick = { navController.navigateUp() }) {
                 Text("Go Back")
             }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ContentNotReadyScreenPreview() {
//    MalankaraOrthodoxLiturgicaTheme {
//        ContentNotReadyScreen(navController = rememberNavController())
//    }
//}