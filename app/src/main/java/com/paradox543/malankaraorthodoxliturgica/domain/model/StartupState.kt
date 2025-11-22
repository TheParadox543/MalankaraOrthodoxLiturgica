package com.paradox543.malankaraorthodoxliturgica.domain.model

sealed class StartupState {
    object Loading : StartupState()

    data class Ready(
        val language: AppLanguage,
        val fontScale: AppFontScale,
        val onboardingCompleted: Boolean,
    ) : StartupState()
}
