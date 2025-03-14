package com.example.malankaraorthodoxliturgica.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.malankaraorthodoxliturgica.model.DataStoreManager
import com.example.malankaraorthodoxliturgica.model.PrayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrayerViewModel(private val repository: PrayerRepository, private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow("ml")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    init {
        // Load stored language from DataStore
        viewModelScope.launch {
            dataStoreManager.selectedLanguage.collect { language ->
                _selectedLanguage.value = language
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
    fun loadTranslations() = repository.loadTranslations(selectedLanguage.value)
    val translations = repository.loadTranslations(selectedLanguage.value)

    fun getCategories() = repository.getCategories()

    private val _categoryPrayers = mutableStateOf<List<String>>(emptyList())
    val categoryPrayers: State<List<String>> = _categoryPrayers

    fun loadCategoryPrayers(category: String) {
        _categoryPrayers.value = repository.getCategoryPrayers(category)
    }

    fun getGreatLentDays() = repository.getGreatLentDays()
    fun getDayPrayers() = repository.getDayPrayers()
    fun getQurbanaSections() = repository.getQurbanaSections()

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
        viewModelScope.launch {
            _prayers.value = repository.loadPrayers(filename, language)
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

    fun getNextPrayer() {
        updateIndex(1)
    }

    fun getPreviousPrayer() {
        updateIndex(-1)
    }
}

class PrayerViewModelFactory(private val repository: PrayerRepository, private val dataStore: DataStoreManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrayerViewModel(repository, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

