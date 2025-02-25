package com.example.malankaraorthodoxliturgica

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp


@Composable
fun HomeScreen(navController: NavController) {
    val prayerCategories = listOf(
        "Daily Prayers" to listOf("Sleeba", "Kyamtha", "Great Lent"),
        "Sacramental Prayers" to listOf("Qurbana", "Baptism", "Wedding", "Funeral"),
        "Feast Day Prayers" to listOf("Christmas", "Easter", "Ascension")

    )

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(prayerCategories) { (category, _) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { navController.navigate("prayer_list/$category") },
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
