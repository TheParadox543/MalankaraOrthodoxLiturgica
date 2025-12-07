package com.paradox543.malankaraorthodoxliturgica.domain.model

import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.AppLanguage

sealed class StartupState {
    object Loading : StartupState()

    data class Ready(
        val language: AppLanguage,
        val fontScale: AppFontScale,
        val onboardingCompleted: Boolean,
        val soundMode: SoundMode,
    ) : StartupState()
}
