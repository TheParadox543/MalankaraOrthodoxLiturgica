package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// DataStore instance
private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // This scope is essential for stateIn to properly manage the StateFlow's lifecycle.
    // It's good practice to use a custom scope for singletons rather than Dispatchers.IO directly.
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val languageKey = stringPreferencesKey("selected_language")
    private val fontSizeKey = intPreferencesKey("font_size")
    private val hasCompletedOnboardingKey = booleanPreferencesKey("has_completed_onboarding")

    // Save language
    suspend fun saveLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[languageKey] = language.code
        }
    }

    suspend fun saveFontSize(fontSize: TextUnit) {
        context.dataStore.edit { preferences ->
            preferences[fontSizeKey] = fontSize.value.toInt()
        }
    }

    suspend fun saveOnboardingStatus(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[hasCompletedOnboardingKey] = completed
        }
    }

    val selectedLanguage: StateFlow<AppLanguage> = context.dataStore.data
        .map { preferences ->
            // Read the string code, then convert to AppLanguage enum
            val code = preferences[languageKey] ?: AppLanguage.MALAYALAM.code
            AppLanguage.fromCode(code) ?: AppLanguage.MALAYALAM // Default to Malayalam if code not found
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Eagerly,
            initialValue = AppLanguage.MALAYALAM // Initial value is also an AppLanguage enum
        )

    val selectedFontSize: StateFlow<TextUnit> = context.dataStore.data // Using the injected dataStore
        .map { preferences ->
            val sizeInt = preferences[fontSizeKey] ?: 16 // Default to basic size
            sizeInt.sp // Convert to TextUnit
        }
        .stateIn(
            scope = repositoryScope, // Use the long-lived scope for the repository
            started = SharingStarted.Eagerly, // Start collecting eagerly when the StateFlow is created
            initialValue = 16.sp // Provide an initial value that will be emitted immediately
        )

    val hasCompletedOnboarding: StateFlow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[hasCompletedOnboardingKey] == true // Default to false if not set
        }
        .stateIn(
            scope = repositoryScope,
            started = SharingStarted.Eagerly, // Eagerly start collecting this vital preference
            initialValue = false // Initial value for the StateFlow
        )
}