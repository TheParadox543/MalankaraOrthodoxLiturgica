package com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReading
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BookNotFoundException
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBiblePrefaceUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleReadingEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatGospelEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.LoadBibleReadingUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.usecase.FormatDateTitleUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository,
    private val translationsRepository: TranslationsRepository,
    private val formatDateTitleUseCase: FormatDateTitleUseCase,
    private val loadBibleReadingUseCase: LoadBibleReadingUseCase,
    private val formatGospelEntryUseCase: FormatGospelEntryUseCase,
    private val formatBiblePrefaceUseCase: FormatBiblePrefaceUseCase,
    private val formatBibleReadingEntryUseCase: FormatBibleReadingEntryUseCase,
) : ViewModel() {
    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = runBlocking { settingsRepository.language.first() },
            )

    private val _translations = MutableStateFlow<Map<String, String>>(emptyMap())
    val translations: StateFlow<Map<String, String>> = _translations.asStateFlow()

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
    val selectedDayViewData: StateFlow<List<LiturgicalEventDetails>> =
        _selectedDayViewData.asStateFlow()

    // State for the currently selected date for UI feedback
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _selectedBibleReference = MutableStateFlow<List<BibleReference>>(listOf())
    val selectedBibleReference: StateFlow<List<BibleReference>> = _selectedBibleReference.asStateFlow()

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
                loadMonth(
                    _currentCalendarViewDate.value.monthValue,
                    _currentCalendarViewDate.value.year,
                )
                loadUpcomingWeekEvents()
            } catch (e: Exception) {
                _error.value = "Failed to load calendar data: ${e.message}"
                System.err.println("Error initializing calendar data: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
        viewModelScope.launch {
            selectedLanguage.collect { language ->
                // When the language changes (from DataStore), load translations
                loadTranslations(language)
            }
        }
    }

    private fun loadTranslations(language: AppLanguage) {
        viewModelScope.launch {
            val loadedTranslations = translationsRepository.loadTranslations(language)
            _translations.update { loadedTranslations }
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

    fun formatGospelEntry(
        entries: List<BibleReference>,
        language: AppLanguage,
    ): String = formatGospelEntryUseCase(entries, language)

    /**
     * Sets the selected BibleReference to be displayed on the BibleReaderScreen.
     * This is called when a user clicks a Bible reading TextButton.
     */
    fun setSelectedBibleReference(reference: List<BibleReference>) {
        _selectedBibleReference.value = reference
    }

    /**
     * Formats a complete BibleReadingEntry (a book with its list of ranges) into a readable string.
     * (e.g., "Matthew 5:1-10, 6:1-5")
     * This function uses the currently selected language from the ViewModel's internal state.
     * @param entry The BibleReadingEntry object containing bookNumber and a list of ranges.
     * @return The formatted string for the entire entry.
     */
    fun formatBibleReadingEntry(
        entry: BibleReference,
        language: AppLanguage,
    ): String = formatBibleReadingEntryUseCase(entry, language)

    fun loadBiblePreface(
        bibleReference: BibleReference,
        language: AppLanguage,
    ): List<PrayerElement.Prose>? = formatBiblePrefaceUseCase(bibleReference, language)

    fun loadBibleReading(
        bibleReferences: List<BibleReference>,
        language: AppLanguage,
    ): BibleReading =
        try {
            val bibleReference = bibleReferences.firstOrNull()
            val preface =
                if (bibleReference != null) {
                    loadBiblePreface(bibleReference, language)
                } else {
                    null
                }
            loadBibleReadingUseCase(bibleReferences, language).copy(preface = preface)
        } catch (e: BookNotFoundException) {
            // Handle the case where a book or chapter is not found
            BibleReading(
                verses =
                    listOf(
                        BibleVerse(
                            0,
                            "Book or chapter not found: ${e.message}",
                        ),
                    ),
            )
        }
}