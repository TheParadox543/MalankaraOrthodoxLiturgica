package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.data.repository.LiturgicalCalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val liturgicalCalendarRepository: LiturgicalCalendarRepository
) : ViewModel() {

    // State for the currently displayed month's calendar data
    private val _monthCalendarData = MutableStateFlow<List<CalendarWeek>>(emptyList())
    val monthCalendarData: StateFlow<List<CalendarWeek>> = _monthCalendarData.asStateFlow()

    // State for upcoming week's events
    private val _upcomingWeekEvents = MutableStateFlow<List<CalendarDay>>(emptyList())
    val upcomingWeekEvents: StateFlow<List<CalendarDay>> = _upcomingWeekEvents.asStateFlow()

    // State for the currently viewed month/year in the calendar UI
    private val _currentCalendarViewDate = MutableStateFlow(LocalDate.now())
    val currentCalendarViewDate: StateFlow<LocalDate> = _currentCalendarViewDate.asStateFlow()

    private val _hasNextMonth = MutableStateFlow(false)
    val hasNextMonth: StateFlow<Boolean> = _hasNextMonth.asStateFlow()

    private val _hasPreviousMonth = MutableStateFlow(false)
    val hasPreviousMonth: StateFlow<Boolean> = _hasPreviousMonth.asStateFlow()

    private val _selectedDayViewData = MutableStateFlow<List<LiturgicalEventDetails>>(emptyList())
    val selectedDayViewData: StateFlow<List<LiturgicalEventDetails>> = _selectedDayViewData.asStateFlow()

    // State for the currently selected date for UI feedback
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    init {
        // Initialize the repository and load initial data
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                liturgicalCalendarRepository.initialize() // Important: Load JSON data first
                loadMonth(_currentCalendarViewDate.value.monthValue, _currentCalendarViewDate.value.year)
                loadUpcomingWeekEvents()
            } catch (e: Exception) {
                _error.value = "Failed to load calendar data: ${e.message}"
                System.err.println("Error initializing calendar data: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMonth(month: Int, year: Int) {
        _isLoading.value = true
        _error.value = null
        try {
            Log.d("CalendarViewModel", "Loading month data for $month/$year")
            _monthCalendarData.value = liturgicalCalendarRepository.loadMonthData(month, year)
            _currentCalendarViewDate.value = LocalDate.of(year, month, 1) // Update viewed month
            val previousMonth = _currentCalendarViewDate.value.minusMonths(1)
            _hasPreviousMonth.value = liturgicalCalendarRepository.checkMonthDataExists(previousMonth.monthValue, previousMonth.year)
            val nextMonth = _currentCalendarViewDate.value.plusMonths(1)
            _hasNextMonth.value = liturgicalCalendarRepository.checkMonthDataExists(nextMonth.monthValue, nextMonth.year)
        } catch (e: Exception) {
            _error.value = "Failed to load month data for $month/$year: ${e.message}"
            System.err.println("Error loading month data: ${e.stackTraceToString()}")
        } finally {
            _isLoading.value = false
        }
    }

    fun loadUpcomingWeekEvents() {
        try {
            _upcomingWeekEvents.value = liturgicalCalendarRepository.getUpcomingWeekEvents()
        } catch (e: Exception) {
            _error.value = "Failed to load upcoming week events: ${e.message}"
            System.err.println("Error loading upcoming week events: ${e.stackTraceToString()}")
        }
    }

    fun setDayEvents(events: List<LiturgicalEventDetails>, date: LocalDate) {
        _selectedDayViewData.value = events
        _selectedDate.value = date // Keep the selected date in sync
    }

    private fun clearDayEvents() {
        _selectedDayViewData.value = emptyList()
        _selectedDate.value = null // Clear the selected date
    }

    fun goToNextMonth() {
        val nextMonthDate = _currentCalendarViewDate.value.plusMonths(1)
        clearDayEvents()
        loadMonth(nextMonthDate.monthValue, nextMonthDate.year)
    }

    fun goToPreviousMonth() {
        val prevMonthDate = _currentCalendarViewDate.value.minusMonths(1)
        clearDayEvents()
        loadMonth(prevMonthDate.monthValue, prevMonthDate.year)
    }

    fun generateYearSuffix(year: Int): String {
        return when (year % 10) {
            1 -> if (year % 100 != 11) "st" else "th"
            2 -> if (year % 100 != 12) "nd" else "th"
            3 -> if (year % 100 != 13) "rd" else "th"
            else -> "th"
        }
    }

    fun generateDateTitle(event: LiturgicalEventDetails, selectedLanguage: AppLanguage): String {
        val currentYear = LocalDate.now().year
        return if (event.startedYear != null) {
            val yearNumber = currentYear - event.startedYear + 1
            val baseYearString = "$yearNumber"

            if (selectedLanguage == AppLanguage.MALAYALAM && event.title.ml != null){
                "$baseYearString-ാം${event.title.ml}"
            } else {
                "$baseYearString${generateYearSuffix(yearNumber)} ${event.title.en}"
            }
        } else when (selectedLanguage) {
            AppLanguage.MALAYALAM -> event.title.ml ?: event.title.en
            else -> event.title.en
        }
    }
}