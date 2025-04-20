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
fun WeddingScreen(navController: NavController, prayerViewModel: PrayerViewModel){
    val translations = prayerViewModel.loadTranslations()
    val selectedFontSize by prayerViewModel.selectedFontSize.collectAsState()
    val sections = prayerViewModel.getWeddingSections()
    LaunchedEffect(Unit) {
        prayerViewModel.setTopBarKeys(listOf("wedding"))
    }

    LazyColumn(modifier = Modifier) {
        items(sections) { section ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        prayerViewModel.setFilename("wedding_${sections.indexOf(section)}.json")
                        prayerViewModel.setTopBarKeys(listOf(section))
                        prayerViewModel.sectionNames = prayerViewModel.getWeddingSections()
                        navController.navigate("prayerScreen")
                    }
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    translations[section] ?: "",
                    fontSize = selectedFontSize,
                    modifier = Modifier.padding(16.dp))
            }
        }
    }
}
