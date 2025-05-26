package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.model.DataStoreManager
import com.paradox543.malankaraorthodoxliturgica.model.PrayerRepository
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
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow("ml")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedFontSize = MutableStateFlow(16.sp) // Default to medium
    val selectedFontSize: StateFlow<TextUnit> = _selectedFontSize.asStateFlow()

    private val _translations = MutableStateFlow<Map<String, String>>(emptyMap())
    val translations: StateFlow<Map<String, String>> = _translations.asStateFlow()

    init {
        // Load stored language from DataStore
        viewModelScope.launch {
            dataStoreManager.selectedLanguage.collect { language ->
                _selectedLanguage.value = language
                loadTranslations(language)
            }
        }
        viewModelScope.launch {
            dataStoreManager.selectedFont.collect{ size ->
                _selectedFontSize.value = size.sp
            }
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch{
            dataStoreManager.saveLanguage(language)
        }
        _selectedLanguage.value = language
        loadTranslations(language)
    }

    fun setFontSize(size: TextUnit) {
        _selectedFontSize.value = size
        viewModelScope.launch {
            dataStoreManager.saveFontSize(size.value.toInt())
        }
    }

    private fun loadTranslations(language: String) {
        viewModelScope.launch {
            val loadedTranslations = prayerRepository.loadTranslations(language)
            _translations.update { loadedTranslations }
        }
    }

    private val _prayers = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val prayers: StateFlow<List<Map<String, Any>>> = _prayers

    fun loadPrayers(filename: String, language: String) {
        try {
            val prayers = prayerRepository.loadPrayers(filename, language)
            _prayers.value = prayers
        } catch (e: Exception) {
            throw e
        }
    }
}

