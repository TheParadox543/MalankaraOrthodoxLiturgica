package com.paradox543.malankaraorthodoxliturgica.ui

import androidx.compose.ui.input.nestedscroll.NestedScrollConnection

/**
 * Describes which top bar, bottom bar, and FAB the single Scaffold in MainActivity should render.
 * Each screen emits one of these via LaunchedEffect to communicate its bar requirements.
 */
sealed class ScaffoldUiState {
    /**
     * Most screens: TopNavBar + optional BottomNavBar.
     */
    data class Standard(
        val title: String,
        val showBottomBar: Boolean = true,
    ) : ScaffoldUiState()

    /**
     * PrayerScreen and BibleChapterScreen: TopNavBar + SectionNavBar with scroll-aware
     * animated show/hide. [showFab] controls whether the QR scan FAB is included.
     */
    data class PrayerReading(
        val title: String,
        val prevRoute: String?,
        val nextRoute: String?,
        val routeProvider: () -> String,
        val isVisible: Boolean,
        val nestedScrollConnection: NestedScrollConnection,
        val showFab: Boolean = true,
    ) : ScaffoldUiState()

    /**
     * Onboarding and other full-screen flows: no bars at all.
     */
    data object None : ScaffoldUiState()
}
