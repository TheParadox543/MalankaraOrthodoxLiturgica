package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.util.Log
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.model.DataStoreManager
import com.paradox543.malankaraorthodoxliturgica.model.NavigationRepository
import com.paradox543.malankaraorthodoxliturgica.model.PrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
   private val prayerRepository: PrayerRepository,
   private val dataStoreManager: DataStoreManager,
   private val navRepository: NavigationRepository
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow("ml")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedFontSize = MutableStateFlow(16.sp) // Default to medium
    val selectedFontSize: StateFlow<TextUnit> = _selectedFontSize//.asStateFlow()

    init {
        // Load stored language from DataStore
        viewModelScope.launch {
            dataStoreManager.selectedLanguage.collect { language ->
                _selectedLanguage.value = language
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
        loadTranslations()
    }

    fun setFontSize(size: TextUnit) {
        _selectedFontSize.value = size
        viewModelScope.launch {
            dataStoreManager.saveFontSize(size.value.toInt())
        }
    }

    fun loadTranslations() = prayerRepository.loadTranslations(selectedLanguage.value)
    val translations = prayerRepository.loadTranslations(selectedLanguage.value)

    private val _topBarNames = MutableStateFlow<List<String>>(emptyList())
    val topBarNames: StateFlow<List<String>> = _topBarNames

    fun setTopBarKeys(keys: List<String>) {
        _topBarNames.value = keys
    }

    var sectionNames: List<String> = listOf()
    fun updateTopBarLastKey(key: Int) {
        _topBarNames.value = _topBarNames.value.dropLast(1) + sectionNames[key]

    }

    private val _prayers = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val prayers: StateFlow<List<Map<String, String>>> = _prayers

    fun loadPrayers(filename: String, language: String) {
        try {
            val prayers = prayerRepository.loadPrayers(filename, language)
            _prayers.value = prayers
        } catch (e: Exception) {
            throw e
        }
    }

    private val _filename = MutableStateFlow("")
    val filename: StateFlow<String> = _filename

    fun setFilename(newFilename: String) {
        _filename.value = newFilename
    }

    fun updateIndex(delta: Int) {
        val regex = Regex("(\\d+)(\\.json)$") // Matches number before ".json"
        val match = regex.find(_filename.value)

        if (match != null) {
            val currentIndex = match.groupValues[1].toInt()
            val newIndex = currentIndex + delta
            if (newIndex >= 0 && newIndex < sectionNames.size) { // Ensure index stays in range
                Log.d("SectionNavigation", "New Index: $newIndex")
                _filename.value = _filename.value.replace(regex, "$newIndex.json")
                updateTopBarLastKey(newIndex)
            }
        }
    }

    private val _sectionNavigation = MutableStateFlow(false)
    val sectionNavigation: StateFlow<Boolean> = _sectionNavigation

    fun setSectionNavigation(enabled: Boolean) {
        _sectionNavigation.value = enabled
    }
}

class PrayerViewModelFactory(private val prayerRepository: PrayerRepository, private val dataStore: DataStoreManager, private val navRepository: NavigationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrayerViewModel(prayerRepository, dataStore, navRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

