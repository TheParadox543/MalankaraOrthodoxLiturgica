package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.data.model.AppFontSize
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val firebaseAnalytics: FirebaseAnalytics,
): ViewModel() {

    val selectedLanguage = settingsRepository.selectedLanguage
    val selectedFontSize = settingsRepository.selectedFontSize
    val hasCompletedOnboarding = settingsRepository.hasCompletedOnboarding
    val songScrollState = settingsRepository.songScrollState

    // Internal MutableStateFlow to track AppFontSize changes for debounced saving
    private val _debouncedAppFontSize = MutableStateFlow(AppFontSize.Medium)

    init {
        // 1. Initialize _currentAppFontSize from DataStore when ViewModel starts
        viewModelScope.launch {
            settingsRepository.selectedFontSize.collectLatest { storedFontSize ->
                _debouncedAppFontSize.value = storedFontSize // Sync the debounced state
            }
        }

        // 2. Debounce mechanism: only save to DataStore after a short delay of no new updates
        viewModelScope.launch {
            _debouncedAppFontSize.collectLatest { fontSizeToSave ->
                delay(500L) // Wait for 500ms for more gesture events to stop
                settingsRepository.saveFontSize(fontSizeToSave) // Then save the enum
            }
        }
    }

    // Function to set (and save) language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(language)
            val bundle = Bundle().apply {
                putString("language", language.name)
            }
            firebaseAnalytics.logEvent("language_selected", bundle)
        }
    }

    // Function to set (and save) font size
    fun setFontSizeFromSettings(size: AppFontSize) {
        viewModelScope.launch {
            settingsRepository.saveFontSize(size) // Convert TextUnit back to Int for DataStore
        }
    }

    fun stepFontSize(direction: Int) {
        settingsRepository.stepFontSize(direction)
    }

    fun logTutorialStart() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
    }

    fun setOnboardingCompleted(status: Boolean = true) {
        viewModelScope.launch {
            settingsRepository.saveOnboardingStatus(status)
            if (status) {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
            }
        }
    }

    fun setSongScrollState(isHorizontal: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveSongScrollState(isHorizontal)
        }
    }
}