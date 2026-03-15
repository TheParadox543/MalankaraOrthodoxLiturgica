package com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.ui.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.ui.viewmodel.PrayerViewModel

@Composable
fun HomeScreen(
    prayerViewModel: PrayerViewModel,
    prayerNavViewModel: PrayerNavViewModel,
    inAppReviewManager: InAppReviewManager,
    contentPadding: PaddingValues,
    onSectionNavigate: (String) -> Unit,
    onScaffoldStateChanged: (ScaffoldUiState) -> Unit,
) {
    val rootNode by prayerNavViewModel.rootNode.collectAsState()
    SectionScreen(
        prayerViewModel,
        rootNode,
        inAppReviewManager,
        contentPadding,
        onScaffoldStateChanged,
        onSectionNavigate = onSectionNavigate,
    )
}
