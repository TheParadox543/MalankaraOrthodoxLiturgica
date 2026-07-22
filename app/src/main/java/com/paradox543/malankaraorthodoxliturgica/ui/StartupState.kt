package com.paradox543.malankaraorthodoxliturgica.ui

sealed class StartupState {
    object Loading : StartupState()

    data class Ready(
        val onboardingStage: Int,
    ) : StartupState()
}