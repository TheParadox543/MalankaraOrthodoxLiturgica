package com.example.malankaraorthodoxliturgica.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.malankaraorthodoxliturgica.model.PrayerRepository
import kotlinx.coroutines.flow.MutableStateFlow

class PrayerViewModel(private val repository: PrayerRepository) : ViewModel() {
    val sectionNavigation = MutableStateFlow(false)
    fun setSectionNavigation(enabled: Boolean) {
        sectionNavigation.value = enabled
    }

    private val _prayers = mutableStateOf<List<String>>(emptyList())
    val prayers: State<List<String>> = _prayers

    fun getCategories() = repository.getCategories()

    fun loadPrayers(category: String) {
        _prayers.value = repository.getPrayers(category)
    }

    fun getGreatLentDays() = repository.getGreatLentDays()
    fun getDayPrayers() = repository.getDayPrayers()
    fun getQurbanaSections() = repository.getQurbanaSections()
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

