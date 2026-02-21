package com.paradox543.malankaraorthodoxliturgica.data.settings.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore instance
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : SettingsRepository {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    val dataStore = context.dataStore

    // DataStore keys
    private val languageKey = stringPreferencesKey("selected_language")
    private val fontScaleKey = floatPreferencesKey("font_scale")
    private val hasCompletedOnboardingKey = booleanPreferencesKey("has_completed_onboarding")
    private val songScrollStateKey = booleanPreferencesKey("song_scroll_state")
    private val soundModeKey = stringPreferencesKey("sound_mode")
    private val soundRestoreDelayKey = intPreferencesKey("sound_restore_delay")

    // Public Flow properties exposed through the interface - directly from DataStore
    override val language: Flow<AppLanguage> =
        context.dataStore.data.map { preferences ->
            val code = preferences[languageKey] ?: AppLanguage.MALAYALAM.code
            AppLanguage.fromCode(code) ?: AppLanguage.MALAYALAM
        }

    override val onboardingCompleted: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
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

    // --- Debouncing for Font Scale ---
    // Debouncing has been moved to ViewModel layer for better separation of concerns

//    init {
//        // Collect from the pendingFontScaleUpdate flow and debounce writes to DataStore
//        repositoryScope.launch {
//            pendingFontScaleUpdate.collectLatest { fontScaleToSave ->
//                debounceJob?.cancel() // Cancel any previous pending save
//                debounceJob =
//                    launch {
//                        delay(200L) // Wait for 200ms after the last update
//                        // Directly call your existing setFontScale function
//                        setFontScale(fontScaleToSave)
//                    }
//            }
//        }
//
//        // Initialize pendingFontScaleUpdate with the current stored font scale when the repository starts.
//        // This ensures debouncing starts from the correct state.
// //        repositoryScope.launch {
// //            selectedFontScale.collectLatest { currentScale ->
// //                pendingFontScaleUpdate.value = currentScale
// //            }
// //        }
//    }

    override suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language.code
        }
    }

    override suspend fun setFontScale(fontScale: AppFontScale) {
        context.dataStore.edit { preferences ->
            preferences[fontScaleKey] = fontScale.scaleFactor
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[hasCompletedOnboardingKey] = completed
        }
    }

    override suspend fun setSongScrollState(isHorizontal: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[songScrollStateKey] = isHorizontal
        }
    }

    override suspend fun setSoundMode(permissionState: SoundMode) {
        context.dataStore.edit { preferences ->
            preferences[soundModeKey] = permissionState.name
        }
    }

    override suspend fun setSoundRestoreDelay(delay: Int) {
        context.dataStore.edit { preferences ->
            preferences[soundRestoreDelayKey] = delay
        }
    }
}

/*
    // Helper function to get version name using context
    fun getPackageInfo(packageManager: PackageManager, packageName: String, flags: Int = 0): String? {
        return try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(flags.toLong())
                ).versionName
            } else {
                packageManager.getPackageInfo(packageName, flags).versionName
            }
        } catch (e: Exception) {
            "Error: $e"
        }
    }
 */
