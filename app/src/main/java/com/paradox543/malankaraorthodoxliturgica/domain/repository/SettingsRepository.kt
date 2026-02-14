package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // 1. One-time startup reads (NO DEFAULTS ALLOWED)
    suspend fun getInitialLanguage(): AppLanguage

    suspend fun getInitialOnboardingCompleted(): Boolean

    suspend fun getInitialFontScale(): AppFontScale

    // 2. Reactive flows for state
    val language: Flow<AppLanguage>
    val onboardingCompleted: Flow<Boolean>
    val fontScale: Flow<AppFontScale>
    val songScrollState: Flow<Boolean>
    val soundMode: Flow<SoundMode>
    val soundRestoreDelay: Flow<Int>

    // 3. Setters for setting options
    suspend fun setLanguage(language: AppLanguage)

    suspend fun setFontScale(fontScale: AppFontScale)

    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun setSongScrollState(isHorizontal: Boolean)

    suspend fun setSoundMode(permissionState: SoundMode)

    suspend fun setSoundRestoreDelay(delay: Int)
}
