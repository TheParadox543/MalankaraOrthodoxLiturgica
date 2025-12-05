package com.paradox543.malankaraorthodoxliturgica.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.PrayerViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    prayerNavViewModel: PrayerNavViewModel,
) {
    val rootNode by prayerNavViewModel.rootNode.collectAsState()
    SectionScreen(navController, prayerViewModel, rootNode)
}
