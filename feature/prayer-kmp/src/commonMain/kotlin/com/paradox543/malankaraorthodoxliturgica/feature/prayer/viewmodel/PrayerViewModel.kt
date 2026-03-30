package com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.loadTranslations
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

class PrayerViewModel(
    private val settingsRepository: SettingsRepository,
    private val translationsRepository: TranslationsRepository,
    private val analyticsService: AnalyticsService,
    private val loadPrayerScreenContent: suspend (String, AppLanguage) -> List<PrayerElement>,
    private val getSongKeyPriority: suspend () -> String,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private data class PrayerLoadRequest(
        val filename: String,
        val language: AppLanguage,
    )

    private val minimumPrayerLoadingIndicatorDuration = 250.milliseconds
    private var prayerLoadJob: Job? = null
    private var inFlightPrayerRequest: PrayerLoadRequest? = null
    private var lastLoadedPrayerRequest: PrayerLoadRequest? = null

    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.MALAYALAM,
        )

    val songScrollState: StateFlow<Boolean> =
        settingsRepository.songScrollState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    private val _translations = MutableStateFlow<Map<String, String>>(emptyMap())
    val translations: StateFlow<Map<String, String>> = _translations.asStateFlow()

    private val _prayers = MutableStateFlow<List<PrayerElement>>(emptyList())
    val prayers: StateFlow<List<PrayerElement>> = _prayers

    private val _isLoadingPrayers = MutableStateFlow(false)
    val isLoadingPrayers: StateFlow<Boolean> = _isLoadingPrayers.asStateFlow()

    private val _dynamicSongKey = MutableStateFlow<String?>(null)
    val dynamicSongKey: StateFlow<String?> = _dynamicSongKey.asStateFlow()

    init {
        // Observe language from SettingsViewModel and trigger translation loading
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

    fun loadPrayerElements(
        filename: String,
        passedLanguage: AppLanguage? = null,
    ) {
        val language: AppLanguage = passedLanguage ?: selectedLanguage.value
        val request = PrayerLoadRequest(filename = filename, language = language)

        // Skip duplicate work when we are already loading or already loaded the same request.
        if (request == inFlightPrayerRequest || (request == lastLoadedPrayerRequest && _prayers.value.isNotEmpty())) {
            return
        }

        inFlightPrayerRequest = request
        prayerLoadJob?.cancel()
        prayerLoadJob = viewModelScope.launch {
            _isLoadingPrayers.value = true
            val loadStartedAt = TimeSource.Monotonic.markNow()
            try {
                val prayers =
                    withContext(backgroundDispatcher) {
                        loadPrayerScreenContent(filename, language)
                    }
                _prayers.value = prayers
                if (_dynamicSongKey.value == null && prayers.any { it is PrayerElement.DynamicSongsBlock }) {
                    _dynamicSongKey.value = withContext(backgroundDispatcher) { getSongKeyPriority() }
                }
                lastLoadedPrayerRequest = request
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _prayers.value = listOf(PrayerElement.Error(e.message ?: "Unknown error"))
                if (inFlightPrayerRequest == request) {
                    lastLoadedPrayerRequest = null
                }
            } finally {
                if (inFlightPrayerRequest == request) {
                    val remainingIndicatorTime = minimumPrayerLoadingIndicatorDuration - loadStartedAt.elapsedNow()
                    if (remainingIndicatorTime.isPositive()) {
                        delay(remainingIndicatorTime)
                    }
                    _isLoadingPrayers.value = false
                    inFlightPrayerRequest = null
                }
            }
        }
    }

    fun setDynamicSongKey(key: String) {
        _dynamicSongKey.value = key
    }

    fun onPrayerSelected(
        prayerName: String,
        prayerId: String,
    ) {
        analyticsService.logEvent(AnalyticsEvent.PrayNowItemSelected(prayerName, prayerId))
    }

    fun reportError(
        errorMessage: String,
        errorLocation: String,
    ) {
        analyticsService.logEvent(AnalyticsEvent.Error(errorMessage, errorLocation))
    }

    fun reportBrokenNavigation(route: String) {
        analyticsService.logEvent(
            AnalyticsEvent.Error(
                "Unknown route: $route",
                route,
            ),
        )
    }

}