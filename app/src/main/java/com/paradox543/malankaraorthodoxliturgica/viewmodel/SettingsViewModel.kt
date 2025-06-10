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

    // Expose language from DataStore as a StateFlow
    // We use stateIn to convert the Flow from DataStore into a StateFlow
    val selectedLanguage: StateFlow<AppLanguage> = settingsRepository.selectedLanguage
        .stateIn(
            scope = viewModelScope,
            // WhileSubscribed ensures the flow is collected as long as there are active collectors
            started = SharingStarted.WhileSubscribed(5_000), // 5s timeout before cancelling upstream
            initialValue = AppLanguage.MALAYALAM // Initial value until DataStore emits
        )

    // Expose font size from DataStore as a StateFlow (converted to TextUnit)
    val selectedFontSize: StateFlow<TextUnit> = settingsRepository.selectedFont
        .map { sizeInt ->
            sizeInt.sp // Convert Int to TextUnit
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 16.sp // Initial value until DataStore emits
        )

    // Function to set (and save) language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(language)
        }
    }

    // Function to set (and save) font size
    fun setFontSize(size: TextUnit) {
        viewModelScope.launch {
            settingsRepository.saveFontSize(size.value.toInt()) // Convert TextUnit back to Int for DataStore
        }
    }
}