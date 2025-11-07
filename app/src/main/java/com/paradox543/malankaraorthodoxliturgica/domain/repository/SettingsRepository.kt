package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.SoundMode
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val selectedLanguage: StateFlow<AppLanguage>

    suspend fun getFontScale(): AppFontScale

    suspend fun getSongScrollState(): Boolean

    suspend fun getSoundMode(): SoundMode

    suspend fun getSoundRestoreDelay(): Int

    suspend fun saveLanguage(language: AppLanguage)

    suspend fun setFontScale(fontScale: AppFontScale)

    suspend fun saveOnboardingStatus(completed: Boolean)

    suspend fun saveSongScrollState(isHorizontal: Boolean)

    suspend fun setSoundMode(permissionState: SoundMode)

    suspend fun setSoundRestoreDelay(delay: Int)
}