package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paradox543.malankaraorthodoxliturgica.data.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// DataStore instance
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsRepository {
    // This scope is essential for stateIn to properly manage the StateFlow's lifecycle.
    // It's good practice to use a custom scope for singletons rather than Dispatchers.IO directly.
    private val Context.dataStore by preferencesDataStore(name = "settings")
    val dataStore = context.dataStore

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // DataStore keys
    private val languageKey = stringPreferencesKey("selected_language")
    private val fontScaleKey = floatPreferencesKey("font_scale")
    private val hasCompletedOnboardingKey = booleanPreferencesKey("has_completed_onboarding")
    private val songScrollStateKey = booleanPreferencesKey("song_scroll_state")
    private val soundModeKey = stringPreferencesKey("sound_mode")
    private val soundRestoreDelayKey = intPreferencesKey("sound_restore_delay")

    // Start up reads
    override suspend fun getInitialLanguage(): AppLanguage {
        val prefs = dataStore.data.first()
        val code = prefs[languageKey] ?: AppLanguage.MALAYALAM.code
        return AppLanguage.fromCode(code) ?: AppLanguage.MALAYALAM
    }

    override suspend fun getInitialOnboardingCompleted(): Boolean {
        val prefs = dataStore.data.first()
        return prefs[hasCompletedOnboardingKey] ?: false
    }

    override val language: StateFlow<AppLanguage> =
        context.dataStore.data
            .map { preferences ->
                // Read the string code, then convert to AppLanguage enum
                val code = preferences[languageKey] ?: AppLanguage.MALAYALAM.code
                AppLanguage.fromCode(code) ?: AppLanguage.MALAYALAM // Default to Malayalam if code not found
            }.stateIn(
                scope = repositoryScope,
                started = SharingStarted.Eagerly,
                initialValue = AppLanguage.MALAYALAM,
            )

    override val onboardingCompleted: StateFlow<Boolean> =
        context.dataStore.data
            .map { preferences ->
                preferences[hasCompletedOnboardingKey] == true
            }.stateIn(
                scope = repositoryScope,
                started = SharingStarted.Eagerly,
                initialValue = false,
            )

    override val fontScale: StateFlow<AppFontScale> =
        dataStore.data
            .map { prefs ->
                val stored = prefs[fontScaleKey] ?: AppFontScale.Medium.scaleFactor
                AppFontScale.fromScale(stored)
            }.stateIn(
                scope = repositoryScope,
                started = SharingStarted.Eagerly,
                initialValue = AppFontScale.Medium,
            )

    override val songScrollState: StateFlow<Boolean> =
        dataStore.data
            .map { prefs ->
                prefs[songScrollStateKey] ?: false
            }.stateIn(
                scope = repositoryScope,
                started = SharingStarted.Eagerly,
                initialValue = false,
            )

    override val soundMode: StateFlow<SoundMode> =
        dataStore.data
            .map { prefs ->
                when (prefs[soundModeKey]) {
                    "SILENT" -> SoundMode.SILENT
                    "DND" -> SoundMode.DND
                    else -> SoundMode.OFF
                }
            }.stateIn(
                scope = repositoryScope,
                started = SharingStarted.Eagerly,
                initialValue = SoundMode.OFF,
            )

    override val soundRestoreDelay: StateFlow<Int> =
        dataStore.data
            .map { prefs ->
                prefs[soundRestoreDelayKey] ?: 30
            }.stateIn(
                scope = repositoryScope,
                started = SharingStarted.Eagerly,
                initialValue = 30,
            )

    // --- Debouncing for Font Scale ---
    // Internal MutableStateFlow to trigger debounced saves for font scale.
    private val pendingFontScaleUpdate = MutableStateFlow(AppFontScale.Medium)
    private var debounceJob: Job? = null // Holds reference to the current debounce coroutine

    init {
        // Collect from the pendingFontScaleUpdate flow and debounce writes to DataStore
        repositoryScope.launch {
            pendingFontScaleUpdate.collectLatest { fontScaleToSave ->
                debounceJob?.cancel() // Cancel any previous pending save
                debounceJob =
                    launch {
                        delay(200L) // Wait for 200ms after the last update
                        // Directly call your existing setFontScale function
                        setFontScale(fontScaleToSave)
                    }
            }
        }

        // Initialize pendingFontScaleUpdate with the current stored font scale when the repository starts.
        // This ensures debouncing starts from the correct state.
//        repositoryScope.launch {
//            selectedFontScale.collectLatest { currentScale ->
//                pendingFontScaleUpdate.value = currentScale
//            }
//        }
    }

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
