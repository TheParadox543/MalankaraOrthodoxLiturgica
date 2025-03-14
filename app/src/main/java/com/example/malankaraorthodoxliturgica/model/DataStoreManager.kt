package com.example.malankaraorthodoxliturgica.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore instance
private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {
    private val LANGUAGE_KEY = stringPreferencesKey("selected_language")
    private val FONT_SIZE_KEY = stringPreferencesKey("font_size")

    // Save language
    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun saveFontSize(fontSize: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = fontSize
        }
    }

    // Read language
    val selectedLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "ml" // Default to Malayalam
        }

    val selectedFont: Flow<Int> = context.dataStore.data
        .map{preferences ->
            preferences[FONT_SIZE_KEY]?.toIntOrNull() ?: 12
        }
}