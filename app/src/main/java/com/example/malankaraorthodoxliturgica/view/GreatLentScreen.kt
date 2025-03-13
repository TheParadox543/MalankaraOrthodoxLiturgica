package com.example.malankaraorthodoxliturgica.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.*
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreatLentScreen(navController: NavController, prayerViewModel: PrayerViewModel) {
    val language by prayerViewModel.selectedLanguage.collectAsState()
    LaunchedEffect(language) {
        prayerViewModel.loadTranslations()
    }
    val translations = prayerViewModel.loadTranslations()
    val days = prayerViewModel.getGreatLentDays()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(translations["great_lent"]?:"") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Page")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(days) { day ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("great_lent_day/$day") }
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(translations[day]?:"", fontSize = 16.sp, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreatLentDayScreen(navController: NavController, prayerViewModel: PrayerViewModel, day: String) {
    val language by prayerViewModel.selectedLanguage.collectAsState()
    LaunchedEffect(language) {
        prayerViewModel.loadTranslations()
    }
    val translations = prayerViewModel.loadTranslations()
    val prayers = prayerViewModel.getDayPrayers()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${translations["great_lent"]?:""} ${translations[day] ?:""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Page"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreatLentPrayerScreen(navController: NavController, prayerViewModel: PrayerViewModel, day: String, prayerIndex: Int) {
    val language by prayerViewModel.selectedLanguage.collectAsState()
    LaunchedEffect(language) {
        prayerViewModel.loadTranslations()
    }
    val translations = prayerViewModel.loadTranslations()
    val prayer = prayerViewModel.getDayPrayers()[prayerIndex]
    val sectionNavigation by prayerViewModel.sectionNavigation.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${translations["great_lent"]?:""} ${translations[day]?:""} ${translations[prayer]?:""}") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Page")
                    }
                }
            )
        }
    ) { padding ->
        PrayerScreen(
            prayerViewModel,
            "great_lent_${day}_$prayerIndex.json",
            Modifier.padding(padding)
        )
    }
}