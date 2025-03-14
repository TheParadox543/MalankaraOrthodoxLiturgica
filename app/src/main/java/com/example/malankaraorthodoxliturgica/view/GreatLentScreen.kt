package com.example.malankaraorthodoxliturgica.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun GreatLentScreen(navController: NavController, prayerViewModel: PrayerViewModel) {
    val translations = prayerViewModel.loadTranslations()
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
                Text(translations[day] ?: "", fontSize = 16.sp, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun GreatLentDayScreen(navController: NavController, prayerViewModel: PrayerViewModel, day: String) {
    val translations = prayerViewModel.loadTranslations()
    val prayers = prayerViewModel.getDayPrayers()
    prayerViewModel.setTopBarKeys(listOf("great_lent", day))

    LazyColumn(modifier = Modifier) {
        items(prayers) { prayer ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(
                            "great_lent_prayer/${day}/${
                                prayers.indexOf(
                                    prayer
                                )
                            }"
                        )
                    }
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    translations[prayer]?:"",
                    fontSize = 16.sp,
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
    PrayerScreen(prayerViewModel)
}