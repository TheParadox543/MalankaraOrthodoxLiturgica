package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.SoundMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    // 1. One-time startup reads (NO DEFAULTS ALLOWED)
    suspend fun getInitialLanguage(): AppLanguage

    suspend fun getInitialOnboardingCompleted(): Boolean

    // 2. Reactive flows for state
    val language: StateFlow<AppLanguage>
    val onboardingCompleted: StateFlow<Boolean>
    val fontScale: StateFlow<AppFontScale>
    val songScrollState: StateFlow<Boolean>
    val soundMode: StateFlow<SoundMode>
    val soundRestoreDelay: StateFlow<Int>

    // 3. Setters for setting options
    suspend fun setLanguage(language: AppLanguage)

    suspend fun setFontScale(fontScale: AppFontScale)

    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun setSongScrollState(isHorizontal: Boolean)

    suspend fun setSoundMode(permissionState: SoundMode)

    suspend fun setSoundRestoreDelay(delay: Int)
}
