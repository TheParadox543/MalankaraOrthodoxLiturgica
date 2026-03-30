package com.paradox543.malankaraorthodoxliturgica.domain.fakes

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Test fake for [SettingsRepository].
 */
class FakeSettingsRepository(
    initialLanguage: AppLanguage = AppLanguage.ENGLISH,
    initialOnboarding: Boolean = false,
    initialFontScale: AppFontScale = AppFontScale.Medium,
    initialSongScroll: Boolean = false,
    initialSoundMode: SoundMode = SoundMode.OFF,
    initialSoundDelay: Int = 0,
) : SettingsRepository {
    private val languageFlow = MutableStateFlow(initialLanguage)
    private val onboardingFlow = MutableStateFlow(initialOnboarding)
    private val fontScaleFlow = MutableStateFlow(initialFontScale)
    private val songScrollFlow = MutableStateFlow(initialSongScroll)
    private val soundModeFlow = MutableStateFlow(initialSoundMode)
    private val soundDelayFlow = MutableStateFlow(initialSoundDelay)

    override val language: Flow<AppLanguage> = languageFlow
    override val onboardingCompleted: Flow<Boolean> = onboardingFlow
    override val fontScale: Flow<AppFontScale> = fontScaleFlow
    override val songScrollState: Flow<Boolean> = songScrollFlow
    override val soundMode: Flow<SoundMode> = soundModeFlow
    override val soundRestoreDelay: Flow<Int> = soundDelayFlow

    override suspend fun setLanguage(language: AppLanguage) {
        languageFlow.value = language
    }

    override suspend fun setFontScale(fontScale: AppFontScale) {
        fontScaleFlow.value = fontScale
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingFlow.value = completed
    }

    override suspend fun setSongScrollState(isHorizontal: Boolean) {
        songScrollFlow.value = isHorizontal
    }

    override suspend fun setSoundMode(permissionState: SoundMode) {
        soundModeFlow.value = permissionState
    }

    override suspend fun setSoundRestoreDelay(delay: Int) {
        soundDelayFlow.value = delay
    }
}
