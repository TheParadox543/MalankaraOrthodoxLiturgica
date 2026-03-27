package com.paradox543.malankaraorthodoxliturgica.feature.prayer.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel

@Composable
fun HomeScreen(
    prayerViewModel: PrayerViewModel,
    prayerNavViewModel: PrayerNavViewModel,
    contentPadding: PaddingValues,
    onSectionNavigate: (String) -> Unit,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
) {
    val rootNode by prayerNavViewModel.rootNode.collectAsState()
    SectionScreen(
        prayerViewModel,
        rootNode,
        contentPadding,
        onScaffoldStateChanged,
        onSectionNavigate = onSectionNavigate,
    )
}
