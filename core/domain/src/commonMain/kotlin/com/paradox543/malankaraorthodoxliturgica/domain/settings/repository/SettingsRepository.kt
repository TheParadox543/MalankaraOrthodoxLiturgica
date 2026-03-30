package com.paradox543.malankaraorthodoxliturgica.domain.settings.repository

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // Reactive flows for state - provides both initial and ongoing values
    val language: Flow<AppLanguage>
    val onboardingCompleted: Flow<Boolean>
    val fontScale: Flow<AppFontScale>
    val songScrollState: Flow<Boolean>
    val soundMode: Flow<SoundMode>
    val soundRestoreDelay: Flow<Int>

    // Setters for updating settings
    suspend fun setLanguage(language: AppLanguage)

    suspend fun setFontScale(fontScale: AppFontScale)

    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun setSongScrollState(isHorizontal: Boolean)

    suspend fun setSoundMode(permissionState: SoundMode)

    suspend fun setSoundRestoreDelay(delay: Int)
}