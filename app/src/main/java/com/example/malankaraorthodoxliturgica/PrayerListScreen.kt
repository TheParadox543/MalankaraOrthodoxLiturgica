package com.example.malankaraorthodoxliturgica

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerListScreen(navController: NavController, category: String) {
    val options = when (category) {
        "Daily Prayers" -> listOf("Sleeba", "Kyamtha", "Great Lent")
        "Sacramental Prayers" -> listOf("Qurbana", "Baptism", "Wedding", "Funeral")
        "Feast Day Prayers" -> listOf("Christmas", "Easter", "Ascension")
        else -> emptyList()
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {Text(category)},
                navigationIcon = {
                    IconButton(
                        onClick = {navController.popBackStack()}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Page"
                        )
                    }
                }
            )
        }
    ){ padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(options) { prayer ->
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("prayer_detail/$prayer") }
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        prayer,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
