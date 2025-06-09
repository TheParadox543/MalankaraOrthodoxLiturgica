package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.SettingsViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    settingsViewModel: SettingsViewModel,
    navViewModel: NavViewModel,
) {
    val rootNode = navViewModel.rootNode
    SectionScreen(navController, prayerViewModel, settingsViewModel, rootNode)
}
