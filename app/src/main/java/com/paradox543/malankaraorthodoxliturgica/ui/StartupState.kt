package com.paradox543.malankaraorthodoxliturgica.ui

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode

sealed class StartupState {
    object Loading : StartupState()

    data class Ready(
        val language: AppLanguage,
        val fontScale: AppFontScale,
        val onboardingCompleted: Boolean,
        val soundMode: SoundMode,
    ) : StartupState()
}