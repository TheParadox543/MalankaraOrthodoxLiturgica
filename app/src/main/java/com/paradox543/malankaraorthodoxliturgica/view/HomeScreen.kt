package com.paradox543.malankaraorthodoxliturgica.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.paradox543.malankaraorthodoxliturgica.viewmodel.NavViewModel
import com.paradox543.malankaraorthodoxliturgica.viewmodel.PrayerViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    prayerViewModel: PrayerViewModel,
    navViewModel: NavViewModel,
) {
    val rootNode = navViewModel.navigationTree
    prayerViewModel.setTopBarKeys(listOf(rootNode.route))
    SectionScreen(
        navController = navController,
        prayerViewModel = prayerViewModel,
        nodes = rootNode.children
    )
}
