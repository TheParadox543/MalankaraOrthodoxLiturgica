package com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel

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
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.loadTranslations
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CalendarViewModel(
    private val calendarRepository: CalendarRepository,
    settingsRepository: SettingsRepository,
    private val translationsRepository: TranslationsRepository,
    private val formatDateTitleUseCase: FormatDateTitleUseCase,
    private val loadBibleReadingUseCase: LoadBibleReadingUseCase,
    private val formatGospelEntryUseCaseLazy: Lazy<FormatGospelEntryUseCase>,
    private val formatBiblePrefaceUseCase: FormatBiblePrefaceUseCase,
    private val formatBibleReadingEntryUseCaseLazy: Lazy<FormatBibleReadingEntryUseCase>,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private data class MonthLoadResult(
        val monthData: List<CalendarWeek>,
        val viewDate: LocalDate,
        val hasPreviousMonth: Boolean,
        val hasNextMonth: Boolean,
    )

    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppLanguage.MALAYALAM,
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
    @OptIn(ExperimentalTime::class)
    private val _currentCalendarViewDate =
        MutableStateFlow(
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date,
        )
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

    private val _selectedBibleReading = MutableStateFlow<BibleReading?>(null)
    val selectedBibleReading: StateFlow<BibleReading?> = _selectedBibleReading.asStateFlow()

    private val _isBibleReadingLoading = MutableStateFlow(false)
    val isBibleReadingLoading: StateFlow<Boolean> = _isBibleReadingLoading.asStateFlow()

    private val _bibleReadingError = MutableStateFlow<String?>(null)
    val bibleReadingError: StateFlow<String?> = _bibleReadingError.asStateFlow()

    private var loadBibleReadingJob: Job? = null

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
                loadMonthInternal(
                    _currentCalendarViewDate.value.month.number,
                    _currentCalendarViewDate.value.year,
                )
                loadUpcomingWeekEventsInternal()
            } catch (e: Exception) {
                _error.value = "Failed to load calendar data: ${e.message}"
//                System.err.println("Error initializing calendar data: ${e.stackTraceToString()}")
            } finally {
                _isLoading.value = false
            }
        }
        viewModelScope.launch {
            selectedLanguage.collectLatest { language ->
                // When the language changes (from DataStore), load translations
                loadTranslations(language)
            }
        }
    }

    private suspend fun loadTranslations(language: AppLanguage) {
        val loadedTranslations = translationsRepository.loadTranslations(language, backgroundDispatcher)
        _translations.update { loadedTranslations }
    }

    fun loadMonth(
        month: Int,
        year: Int,
    ) {
        viewModelScope.launch {
            loadMonthInternal(month, year)
        }
    }

    private suspend fun loadMonthInternal(
        month: Int,
        year: Int,
    ) {
        _isLoading.value = true
        _error.value = null
        try {
            val result =
                withContext(backgroundDispatcher) {
                    val monthData = calendarRepository.loadMonthData(month, year)
                    val viewDate = LocalDate(year, month, 1)
                    val previousMonth = viewDate.plus(-1, DateTimeUnit.MONTH)
                    val hasPreviousMonth =
                        calendarRepository.checkMonthDataExists(
                            previousMonth.month.number,
                            previousMonth.year,
                        )
                    val nextMonth = viewDate.plus(1, DateTimeUnit.MONTH)
                    val hasNextMonth =
                        calendarRepository.checkMonthDataExists(
                            nextMonth.month.number,
                            nextMonth.year,
                        )
                    MonthLoadResult(
                        monthData = monthData,
                        viewDate = viewDate,
                        hasPreviousMonth = hasPreviousMonth,
                        hasNextMonth = hasNextMonth,
                    )
                }

            _monthCalendarData.value = result.monthData
            _currentCalendarViewDate.value = result.viewDate
            _hasPreviousMonth.value = result.hasPreviousMonth
            _hasNextMonth.value = result.hasNextMonth
        } catch (e: Exception) {
            _error.value = "Failed to load month data for $month/$year: ${e.message}"
            println("Error loading month data: ${e.stackTraceToString()}")
        } finally {
            _isLoading.value = false
        }
    }

    fun loadUpcomingWeekEvents() {
        viewModelScope.launch {
            loadUpcomingWeekEventsInternal()
        }
    }

    private suspend fun loadUpcomingWeekEventsInternal() {
        try {
            val events =
                withContext(backgroundDispatcher) {
                    calendarRepository.getUpcomingWeekEvents()
                }
            _upcomingWeekEvents.value = events
        } catch (e: Exception) {
            _error.value = "Failed to load upcoming week events: ${e.message}"
            println("Error loading upcoming week events: ${e.stackTraceToString()}")
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
        val nextMonthDate = _currentCalendarViewDate.value.plus(1, DateTimeUnit.MONTH)
        clearDayEvents()
        loadMonth(nextMonthDate.month.number, nextMonthDate.year)
    }

    fun goToPreviousMonth() {
        val prevMonthDate = _currentCalendarViewDate.value.plus(-1, DateTimeUnit.MONTH)
        clearDayEvents()
        loadMonth(prevMonthDate.month.number, prevMonthDate.year)
    }

    fun getFormattedDateTitle(
        event: LiturgicalEventDetails,
        selectedLanguage: AppLanguage,
    ): String = formatDateTitleUseCase(event, selectedLanguage, _currentCalendarViewDate.value.year)

    fun formatGospelEntry(
        entries: List<BibleReference>,
        language: AppLanguage,
    ): String = formatGospelEntryUseCaseLazy.value(entries, language)

    /**
     * Sets the selected BibleReference to be displayed on the BibleReaderScreen.
     * This is called when a user clicks a Bible reading TextButton.
     */
    fun setSelectedBibleReference(reference: List<BibleReference>) {
        _selectedBibleReference.value = reference
        _selectedBibleReading.value = null
        _bibleReadingError.value = null
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
    ): String = formatBibleReadingEntryUseCaseLazy.value(entry, language)

    suspend fun loadBiblePreface(
        bibleReference: BibleReference,
        language: AppLanguage,
    ): List<PrayerElement.Prose>? = formatBiblePrefaceUseCase(bibleReference, language)

    fun loadSelectedBibleReading(
        bibleReferences: List<BibleReference>,
        language: AppLanguage,
    ) {
        loadBibleReadingJob?.cancel()

        if (bibleReferences.isEmpty()) {
            _selectedBibleReading.value = null
            _bibleReadingError.value = null
            _isBibleReadingLoading.value = false
            return
        }

        loadBibleReadingJob =
            viewModelScope.launch {
                _isBibleReadingLoading.value = true
                _bibleReadingError.value = null
                try {
                    val reading =
                        withContext(backgroundDispatcher) {
                            buildBibleReading(bibleReferences, language)
                        }
                    _selectedBibleReading.value = reading
                } catch (e: Exception) {
                    _bibleReadingError.value = "Failed to load Bible reading: ${e.message}"
                    _selectedBibleReading.value = null
                } finally {
                    _isBibleReadingLoading.value = false
                }
            }
    }

    private suspend fun buildBibleReading(
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
