package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val prayerRepository: PrayerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val selectedLanguage = settingsRepository.selectedLanguage

    private val _translations = MutableStateFlow<Map<String, String>>(emptyMap())
    val translations: StateFlow<Map<String, String>> = _translations.asStateFlow()

    private val _prayers = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val prayers: StateFlow<List<Map<String, Any>>> = _prayers

    init {
        // Observe language from SettingsViewModel and trigger translation loading
        viewModelScope.launch {
            selectedLanguage.collect { language ->
                // When the language changes (from DataStore), load translations
                loadTranslations(language)
            }
        }
    }

    private fun loadTranslations(language: AppLanguage) {
        viewModelScope.launch {
            val loadedTranslations = prayerRepository.loadTranslations(language)
            _translations.update { loadedTranslations }
        }
    }

    // This function can remain, but it should likely take language from `settingsViewModel.selectedLanguage.value`
    // at the time it's called, or you can pass it from the UI.
    // If filename depends on language, this might need more thought.
    fun loadPrayers(filename: String) {
        viewModelScope.launch { // Launch in ViewModelScope for async operation
            try {
                // Access the current language from SettingsViewModel
                val language = selectedLanguage.value
                val prayers = prayerRepository.loadPrayers(filename, language)
                _prayers.value = prayers
            } catch (e: Exception) {
                // Consider more robust error handling (e.g., expose to UI via StateFlow)
                throw e
            }
        }
    }
}

