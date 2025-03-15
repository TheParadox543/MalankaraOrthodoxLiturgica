package com.example.malankaraorthodoxliturgica.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun CategoryListScreen(navController: NavController, prayerViewModel: PrayerViewModel, category: String) {
    val translations = prayerViewModel.loadTranslations()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    val prayers by prayerViewModel.categoryPrayers

    LaunchedEffect(category) {
        prayerViewModel.loadCategoryPrayers(category)
    }
    prayerViewModel.setTopBarKeys(listOf(category))
    LazyColumn(modifier = Modifier) {
        items(prayers) { prayer ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (prayer) {
                            "great_lent" -> navController.navigate("great_lent_main")
                            "nineveh" -> navController.navigate("nineveh_lent_main")
                            "qurbana" -> navController.navigate("qurbana")
                            else -> navController.navigate("dummy")
                        }
                    }
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    translations[prayer]?:"",
                    fontSize = selectedFontSize,
                    modifier = Modifier.padding(16.dp))
            }
        }
    }
}
