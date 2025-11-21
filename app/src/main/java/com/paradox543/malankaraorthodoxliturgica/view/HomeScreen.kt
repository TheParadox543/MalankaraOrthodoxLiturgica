package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    settingsViewModel: SettingsViewModel,
    prayerNavViewModel: PrayerNavViewModel,
) {
    val rootNode by prayerNavViewModel.rootNode.collectAsState()
    SectionScreen(navController, prayerViewModel, settingsViewModel, rootNode)
}
