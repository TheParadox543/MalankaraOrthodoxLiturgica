package com.paradox543.malankaraorthodoxliturgica.data.settings.repository

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.OnboardingStage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Foundation.NSUserDefaults

private const val LANGUAGE_KEY = "selected_language"
private const val FONT_SCALE_KEY = "font_scale"
private const val ONBOARDING_STAGE_KEY = "onboarding_stage"
private const val SONG_SCROLL_STATE_KEY = "song_scroll_state"
private const val SOUND_MODE_KEY = "sound_mode"
private const val SOUND_RESTORE_DELAY_KEY = "sound_restore_delay"

/**
 * Persists settings via NSUserDefaults, mirroring AndroidSettingsRepository's
 * DataStore-backed semantics (same keys' intent, same onboardingCompleted
 * derivation from onboardingStage) with MutableStateFlow instead of
 * Flow-mapped-over-DataStore, since callers already expect StateFlow-shaped
 * properties from the constructor-default pattern this class used before.
 */
class IOSSettingsRepository(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : SettingsRepository {
    override val language: MutableStateFlow<AppLanguage> =
        MutableStateFlow(
            defaults.stringForKey(LANGUAGE_KEY)?.let { AppLanguage.fromCode(it) } ?: AppLanguage.MALAYALAM,
        )

    override val fontScale: MutableStateFlow<AppFontScale> =
        MutableStateFlow(
            if (defaults.objectForKey(FONT_SCALE_KEY) != null) {
                AppFontScale.fromScale(defaults.floatForKey(FONT_SCALE_KEY))
            } else {
                AppFontScale.Medium
            },
        )

    override val onboardingStage: MutableStateFlow<Int> =
        MutableStateFlow(defaults.integerForKey(ONBOARDING_STAGE_KEY).toInt())

    override val onboardingCompleted: MutableStateFlow<Boolean> =
        MutableStateFlow(onboardingStage.value >= OnboardingStage.COMPLETE.value)

    override val songScrollState: MutableStateFlow<Boolean> =
        MutableStateFlow(defaults.boolForKey(SONG_SCROLL_STATE_KEY))

    override val soundMode: MutableStateFlow<SoundMode> =
        MutableStateFlow(
            when (defaults.stringForKey(SOUND_MODE_KEY)) {
                "SILENT" -> SoundMode.SILENT
                "DND" -> SoundMode.DND
                else -> SoundMode.OFF
            },
        )

    override val soundRestoreDelay: MutableStateFlow<Int> =
        MutableStateFlow(
            if (defaults.objectForKey(SOUND_RESTORE_DELAY_KEY) != null) {
                defaults.integerForKey(SOUND_RESTORE_DELAY_KEY).toInt()
            } else {
                30
            },
        )

    override suspend fun setLanguage(language: AppLanguage) {
        defaults.setObject(language.code, forKey = LANGUAGE_KEY)
        this.language.value = language
    }

    override suspend fun setFontScale(fontScale: AppFontScale) {
        defaults.setFloat(fontScale.scaleFactor, forKey = FONT_SCALE_KEY)
        this.fontScale.value = fontScale
    }

    override suspend fun setOnboardingStage(stage: Int) {
        defaults.setInteger(stage.toLong(), forKey = ONBOARDING_STAGE_KEY)
        onboardingStage.value = stage
        onboardingCompleted.value = stage >= OnboardingStage.COMPLETE.value
    }

    override suspend fun setSongScrollState(isHorizontal: Boolean) {
        defaults.setBool(isHorizontal, forKey = SONG_SCROLL_STATE_KEY)
        songScrollState.value = isHorizontal
    }

    override suspend fun setSoundMode(permissionState: SoundMode) {
        defaults.setObject(permissionState.name, forKey = SOUND_MODE_KEY)
        soundMode.value = permissionState
    }

    override suspend fun setSoundRestoreDelay(delay: Int) {
        defaults.setInteger(delay.toLong(), forKey = SOUND_RESTORE_DELAY_KEY)
        soundRestoreDelay.value = delay
    }
}
