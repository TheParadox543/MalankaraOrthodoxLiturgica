package com.example.malankaraorthodoxliturgica.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.malankaraorthodoxliturgica.model.PrayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrayerViewModel(private val repository: PrayerRepository) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow("ml")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }
    fun loadTranslations() = repository.loadTranslations(selectedLanguage.value)

    fun getCategories() = repository.getCategories()

    private val _categoryPrayers = mutableStateOf<List<String>>(emptyList())
    val categoryPrayers: State<List<String>> = _categoryPrayers

    fun loadCategoryPrayers(category: String) {
        _categoryPrayers.value = repository.getCategoryPrayers(category)
    }

    fun getGreatLentDays() = repository.getGreatLentDays()
    fun getDayPrayers() = repository.getDayPrayers()
    fun getQurbanaSections() = repository.getQurbanaSections()

    private val _prayers = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val prayers: StateFlow<List<Map<String, String>>> = _prayers

    fun loadPrayers(filename: String, language: String) {
        viewModelScope.launch {
            _prayers.value = repository.loadPrayers(filename, language)
        }
    }

    private val _sectionNavigation = MutableStateFlow(false)
    val sectionNavigation: StateFlow<Boolean> = _sectionNavigation

    fun setSectionNavigation(enabled: Boolean) {
        _sectionNavigation.value = enabled
    }

    fun getNextPrayer(currentDay: String, currentPrayer: String): Pair<String, String>? {
        val days = getGreatLentDays()
        val prayers = getDayPrayers()

        val dayIndex = days.indexOf(currentDay)
        val prayerIndex = prayers.indexOf(currentPrayer)

        if (dayIndex == -1 || prayerIndex == -1) return null

        return when {
            prayerIndex < prayers.lastIndex -> currentDay to prayers[prayerIndex + 1] // Move to next prayer
            dayIndex < days.lastIndex -> days[dayIndex + 1] to prayers.first() // Move to next day
            else -> null // No next prayer (Friday 9th Hour)
        }
    }

    fun getPreviousPrayer(currentDay: String, currentPrayer: String): Pair<String, String>? {
        val days = getGreatLentDays()
        val prayers = getDayPrayers()

        val dayIndex = days.indexOf(currentDay)
        val prayerIndex = prayers.indexOf(currentPrayer)

        if (dayIndex == -1 || prayerIndex == -1) return null

        return when {
            prayerIndex > 0 -> currentDay to prayers[prayerIndex - 1] // Move to previous prayer
            dayIndex > 0 -> days[dayIndex - 1] to prayers.last() // Move to previous day
            else -> null // No previous prayer (Monday Sandhya)
        }
    }
}

class PrayerViewModelFactory(private val repository: PrayerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PrayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PrayerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

