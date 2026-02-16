package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.usecase.FormatDateTitleUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val formatDateTitleUseCase: FormatDateTitleUseCase,
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

    fun loadMonth(
        month: Int,
        year: Int,
    ) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                Log.d("CalendarViewModel", "Loading month data for $month/$year")
                _monthCalendarData.value = calendarRepository.loadMonthData(month, year)
                _currentCalendarViewDate.value = LocalDate.of(year, month, 1) // Update viewed month
                val previousMonth = _currentCalendarViewDate.value.minusMonths(1)
                _hasPreviousMonth.value =
                    calendarRepository.checkMonthDataExists(
                        previousMonth.monthValue,
                        previousMonth.year,
                    )
                val nextMonth = _currentCalendarViewDate.value.plusMonths(1)
                _hasNextMonth.value =
                    calendarRepository.checkMonthDataExists(nextMonth.monthValue, nextMonth.year)
            } catch (e: Exception) {
                _error.value = "Failed to load month data for $month/$year: ${e.message}"
                System.err.println("Error loading month data: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUpcomingWeekEvents() {
        try {
            _upcomingWeekEvents.value = calendarRepository.getUpcomingWeekEvents()
        } catch (e: Exception) {
            _error.value = "Failed to load upcoming week events: ${e.message}"
            System.err.println("Error loading upcoming week events: ${e.stackTraceToString()}")
        }
    }

    fun setDayEvents(
        events: List<LiturgicalEventDetails>,
        date: LocalDate,
    ) {
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

    fun getFormattedDateTitle(
        event: LiturgicalEventDetails,
        selectedLanguage: AppLanguage,
    ): String = formatDateTitleUseCase(event, selectedLanguage)
}