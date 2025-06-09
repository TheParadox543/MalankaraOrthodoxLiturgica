package com.paradox543.malankaraorthodoxliturgica.viewmodel

//import androidx.lifecycle.ViewModel
//import com.paradox543.malankaraorthodoxliturgica.data.model.DateKeysInMonth
//import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetails
//import com.paradox543.malankaraorthodoxliturgica.data.repository.CalendarRepository
//
//import javax.inject.Inject;
//import dagger.hilt.android.lifecycle.HiltViewModel;
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import java.time.YearMonth
//
//@HiltViewModel
//class CalendarViewModel @Inject constructor(
//    private val calendarRepository: CalendarRepository
//): ViewModel() {
//    // Represents the currently displayed month (e.g., May 2025)
//    private val _currentMonth = MutableStateFlow(YearMonth.now())
//    val currentMonth: StateFlow<YearMonth> = _currentMonth
//
//    // Maps day (Int) to a list of its event keys. Null if no data for the month.
//    // Example: { 1: ["FEAST_ST_JOSEPH"], 15: ["COMM_ST_MARY"] }
//    private val _monthEventKeys = MutableStateFlow<Map<Int, DateKeysInMonth?>>(emptyMap())
//    val monthEventKeys: StateFlow<Map<Int, DateKeysInMonth?>> = _monthEventKeys
//
//    // This will hold the detailed events for a specific selected day (e.g., when user clicks a date)
//    private val _selectedDayEvents = MutableStateFlow<List<LiturgicalEventDetails>>(emptyList())
//    val selectedDayEvents: StateFlow<List<LiturgicalEventDetails>> = _selectedDayEvents
//
//    init {
//        // Load data for the initial current month when the ViewModel is created
//        loadMonthData(_currentMonth.value)
//        // Also load events for today by default
//        loadDayEvents(LocalDate.now().dayOfMonth)
//    }
//
//    /**
//     * Loads the liturgical event keys for the specified YearMonth.
//     * Updates the _monthEventKeys StateFlow.
//     */
//    private fun loadMonthData(yearMonth: YearMonth) {
//        viewModelScope.launch {
//            val year = yearMonth.year
//            val month = yearMonth.monthValue // 1-indexed month
//            val keys = calendarRepository.getMonthKeys(year, month)
//
//            // Convert Map<String, DayKeysForDate?> to Map<Int, DayKeysForDate?>
//            // for easier use with LocalDate.dayOfMonth (which is Int)
//            val monthMap = keys?.mapKeys { it.key.toInt() } ?: emptyMap()
//            _monthEventKeys.value = monthMap
//        }
//    }
//
//    /**
//     * Loads the detailed liturgical events for a specific day within the current month.
//     * Updates the _selectedDayEvents StateFlow.
//     */
//    fun loadDayEvents(day: Int) {
//        viewModelScope.launch {
//            val currentYearMonth = _currentMonth.value
//            val events = calendarRepository.getLiturgicalDataForDate(
//                currentYearMonth.year,
//                currentYearMonth.monthValue,
//                day
//            )
//            _selectedDayEvents.value = events
//        }
//    }
//
//    /**
//     * Navigates to the next month and loads its data.
//     */
//    fun goToNextMonth() {
//        _currentMonth.value = _currentMonth.value.plusMonths(1)
//        loadMonthData(_currentMonth.value)
//        // When month changes, clear day events or load for a specific day in the new month (e.g., day 1)
//        loadDayEvents(1)
//    }
//
//    /**
//     * Navigates to the previous month and loads its data.
//     */
//    fun goToPreviousMonth() {
//        _currentMonth.value = _currentMonth.value.minusMonths(1)
//        loadMonthData(_currentMonth.value)
//        // When month changes, clear day events or load for a specific day in the new month (e.g., day 1)
//        loadDayEvents(1)
//    }
//
//    /**
//     * Navigates to a specific month and loads its data.
//     */
//    fun goToMonth(yearMonth: YearMonth) {
//        _currentMonth.value = yearMonth
//        loadMonthData(_currentMonth.value)
//        // When navigating to a specific month, default to loading events for day 1
//        loadDayEvents(1)
//    }
//}
//}