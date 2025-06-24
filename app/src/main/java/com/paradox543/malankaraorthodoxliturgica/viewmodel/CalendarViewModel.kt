package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDay
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
                loadMonth(LocalDate.now().monthValue, LocalDate.now().year)
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
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _monthCalendarData.value = liturgicalCalendarRepository.loadMonthData(month, year)
                _currentCalendarViewDate.value = LocalDate.of(year, month, 1) // Update viewed month
            } catch (e: Exception) {
                _error.value = "Failed to load month data for $month/$year: ${e.message}"
                System.err.println("Error loading month data: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUpcomingWeekEvents() {
        viewModelScope.launch {
            _error.value = null // Clear previous errors for this operation
            try {
                _upcomingWeekEvents.value = liturgicalCalendarRepository.getUpcomingWeekEvents()
            } catch (e: Exception) {
                _error.value = "Failed to load upcoming week events: ${e.message}"
                System.err.println("Error loading upcoming week events: ${e.stackTraceToString()}")
            }
        }
    }

    fun goToNextMonth() {
        val nextMonthDate = _currentCalendarViewDate.value.plusMonths(1)
        loadMonth(nextMonthDate.monthValue, nextMonthDate.year)
    }

    fun goToPreviousMonth() {
        val prevMonthDate = _currentCalendarViewDate.value.minusMonths(1)
        loadMonth(prevMonthDate.monthValue, prevMonthDate.year)
    }
}