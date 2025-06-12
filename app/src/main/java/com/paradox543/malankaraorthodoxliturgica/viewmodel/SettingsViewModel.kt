package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.AppFontSize
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    val selectedLanguage = settingsRepository.selectedLanguage
    val selectedFontSize = settingsRepository.selectedFontSize
    val hasCompletedOnboarding = settingsRepository.hasCompletedOnboarding

    // Function to set (and save) language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(language)
        }
    }

    // Function to set (and save) font size
    fun setFontSize(size: AppFontSize) {
        viewModelScope.launch {
            settingsRepository.saveFontSize(size) // Convert TextUnit back to Int for DataStore
        }
    }

    fun setOnboardingCompleted(status: Boolean = true) {
        viewModelScope.launch {
            settingsRepository.saveOnboardingStatus(status)
        }
    }
}