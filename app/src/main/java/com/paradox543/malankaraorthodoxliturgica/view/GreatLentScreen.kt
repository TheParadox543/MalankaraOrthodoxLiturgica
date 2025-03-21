package com.paradox543.malankaraorthodoxliturgica.view

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
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun GreatLentScreen(navController: NavController, prayerViewModel: PrayerViewModel) {
    val translations = prayerViewModel.loadTranslations()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    val days = prayerViewModel.getGreatLentDays()
    prayerViewModel.setTopBarKeys(listOf("great_lent"))

    LazyColumn(modifier = Modifier) {
        items(days) { day ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("great_lent_day/$day") }
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    translations[day] ?: "",
                    fontSize = selectedFontSize,
                    modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun GreatLentDayScreen(navController: NavController, prayerViewModel: PrayerViewModel, day: String) {
    val translations = prayerViewModel.loadTranslations()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    val prayers = prayerViewModel.getDayPrayers()
    LaunchedEffect(day) {
        prayerViewModel.setTopBarKeys(listOf("great_lent", day))
    }

    LazyColumn(modifier = Modifier) {
        items(prayers) { prayer ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        prayerViewModel.setFilename("great_lent_${day}_${prayers.indexOf(prayer)}.json")
                        prayerViewModel.setTopBarKeys(listOf("great_lent", day, prayer))
                        prayerViewModel.sectionNames = prayerViewModel.getDayPrayers()
                        navController.navigate(
                            "prayerScreen"
                        )
                    }
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    translations[prayer]?:"",
                    fontSize = selectedFontSize,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun GreatLentPrayerScreen(navController: NavController, prayerViewModel: PrayerViewModel, day: String, prayerIndex: Int) {
    prayerViewModel.setFilename("great_lent_${day}_$prayerIndex.json")
    prayerViewModel.setTopBarKeys(listOf("great_lent", day, prayerViewModel.getDayPrayers()[prayerIndex]))
    prayerViewModel.sectionNames = prayerViewModel.getDayPrayers()
    PrayerScreen(navController, prayerViewModel)
}