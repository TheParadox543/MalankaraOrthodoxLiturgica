package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.data.model.AppFontSize
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    fun setFontSize(size: AppFontSize) {
        viewModelScope.launch {
            settingsRepository.saveFontSize(size) // Convert TextUnit back to Int for DataStore
        }
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