package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    val selectedLanguage: StateFlow<AppLanguage> = settingsRepository.selectedLanguage
    val selectedFontSize: StateFlow<TextUnit> = settingsRepository.selectedFontSize
    val hasCompletedOnboarding: StateFlow<Boolean> = settingsRepository.hasCompletedOnboarding

    // Function to set (and save) language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(language)
        }
    }

    // Function to set (and save) font size
    fun setFontSize(size: TextUnit) {
        viewModelScope.launch {
            settingsRepository.saveFontSize(size) // Convert TextUnit back to Int for DataStore
        }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            settingsRepository.saveOnboardingStatus(true)
        }
    }
}