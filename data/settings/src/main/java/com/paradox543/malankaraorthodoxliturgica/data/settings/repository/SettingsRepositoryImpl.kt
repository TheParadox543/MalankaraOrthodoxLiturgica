package com.paradox543.malankaraorthodoxliturgica.data.settings.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    // DataStore keys
    private val languageKey = stringPreferencesKey("selected_language")
    private val fontScaleKey = floatPreferencesKey("font_scale")
    private val hasCompletedOnboardingKey = booleanPreferencesKey("has_completed_onboarding")
    private val songScrollStateKey = booleanPreferencesKey("song_scroll_state")
    private val soundModeKey = stringPreferencesKey("sound_mode")
    private val soundRestoreDelayKey = intPreferencesKey("sound_restore_delay")

    override val language: Flow<AppLanguage> =
        dataStore.data.map { preferences ->
            val code = preferences[languageKey] ?: AppLanguage.MALAYALAM.code
            AppLanguage.fromCode(code) ?: AppLanguage.MALAYALAM
        }

    override val onboardingCompleted: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[hasCompletedOnboardingKey] == true
        }

    override val fontScale: Flow<AppFontScale> =
        dataStore.data.map { prefs ->
            val stored = prefs[fontScaleKey] ?: AppFontScale.Medium.scaleFactor
            AppFontScale.fromScale(stored)
        }

    override val songScrollState: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[songScrollStateKey] ?: false
        }

    override val soundMode: Flow<SoundMode> =
        dataStore.data.map { prefs ->
            when (prefs[soundModeKey]) {
                "SILENT" -> SoundMode.SILENT
                "DND" -> SoundMode.DND
                else -> SoundMode.OFF
            }
        }

    override val soundRestoreDelay: Flow<Int> =
        dataStore.data.map { prefs ->
            prefs[soundRestoreDelayKey] ?: 30
        }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language.code
        }
    }

    override suspend fun setFontScale(fontScale: AppFontScale) {
        dataStore.edit { preferences ->
            preferences[fontScaleKey] = fontScale.scaleFactor
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[hasCompletedOnboardingKey] = completed
        }
    }

    override suspend fun setSongScrollState(isHorizontal: Boolean) {
        dataStore.edit { preferences ->
            preferences[songScrollStateKey] = isHorizontal
        }
    }

    override suspend fun setSoundMode(permissionState: SoundMode) {
        dataStore.edit { preferences ->
            preferences[soundModeKey] = permissionState.name
        }
    }

    override suspend fun setSoundRestoreDelay(delay: Int) {
        dataStore.edit { preferences ->
            preferences[soundRestoreDelayKey] = delay
        }
    }
}
