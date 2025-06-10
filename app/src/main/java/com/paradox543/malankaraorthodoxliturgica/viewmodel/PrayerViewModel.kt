package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElement
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
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val selectedLanguage = settingsRepository.selectedLanguage

    private val _translations = MutableStateFlow<Map<String, String>>(emptyMap())
    val translations: StateFlow<Map<String, String>> = _translations.asStateFlow()

    private val _prayers = MutableStateFlow<List<PrayerElement>>(emptyList())
    val prayers: StateFlow<List<PrayerElement>> = _prayers

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

    fun loadPrayerElements(filename: String) {
        viewModelScope.launch { // Launch in ViewModelScope for async operation
            try {
                // Access the current language from SettingsViewModel
                val language = selectedLanguage.value
                val prayers = prayerRepository.loadPrayerElements(filename, language)
                _prayers.value = prayers
            } catch (e: Exception) {
                // Consider more robust error handling (e.g., expose to UI via StateFlow)
//                throw e
                _prayers.value = listOf(PrayerElement.Error(e.message ?: "Unknown error"))
            }
        }
    }
}

