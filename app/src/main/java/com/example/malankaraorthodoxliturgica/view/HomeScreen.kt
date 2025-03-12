package com.example.malankaraorthodoxliturgica.view

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun HomeScreen(navController: NavController, prayerViewModel: PrayerViewModel) {
    val language by prayerViewModel.selectedLanguage.collectAsState()
    LaunchedEffect(language) {
        prayerViewModel.selectedLanguage
    }
    val translations = prayerViewModel.loadTranslations()
    val categories = prayerViewModel.getCategories()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(categories) { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { navController.navigate("prayer_list/$category") },
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = translations[category]?:"",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
