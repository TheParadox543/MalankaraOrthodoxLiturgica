package com.paradox543.malankaraorthodoxliturgica.data.settings.repository

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow

class IOSSettingsRepository(
    override val fontScale: MutableStateFlow<AppFontScale> = MutableStateFlow(AppFontScale.Medium),
    override val language: MutableStateFlow<AppLanguage> = MutableStateFlow(AppLanguage.MALAYALAM),
    override val onboardingCompleted: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val songScrollState: MutableStateFlow<Boolean> = MutableStateFlow(false),
    override val soundMode: MutableStateFlow<SoundMode> = MutableStateFlow(SoundMode.OFF),
    override val soundRestoreDelay: MutableStateFlow<Int> = MutableStateFlow(30),
) : SettingsRepository {
    override suspend fun setLanguage(language: AppLanguage) {
        TODO("Not yet implemented")
    }

    override suspend fun setFontScale(fontScale: AppFontScale) {
        TODO("Not yet implemented")
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setSongScrollState(isHorizontal: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun setSoundMode(permissionState: SoundMode) {
        TODO("Not yet implemented")
    }

    override suspend fun setSoundRestoreDelay(delay: Int) {
        TODO("Not yet implemented")
    }
}