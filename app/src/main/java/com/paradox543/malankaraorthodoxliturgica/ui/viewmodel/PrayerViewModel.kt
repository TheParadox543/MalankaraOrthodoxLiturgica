package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.GetPrayerScreenContentUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.GetSongKeyPriorityUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.LoadTranslationsUseCase
import com.paradox543.malankaraorthodoxliturgica.services.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.services.InAppReviewManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    private val analyticsService: AnalyticsService,
    private val inAppReviewManager: InAppReviewManager,
    private val loadTranslationsUseCase: LoadTranslationsUseCase,
    private val getPrayerScreenContentUseCase: GetPrayerScreenContentUseCase,
    private val getSongKeyPriorityUseCase: GetSongKeyPriorityUseCase,
) : ViewModel() {
    val selectedLanguage = settingsRepository.language

    private val _translations = MutableStateFlow<Map<String, String>>(emptyMap())
    val translations: StateFlow<Map<String, String>> = _translations.asStateFlow()

    private val _prayers = MutableStateFlow<List<PrayerElementDomain>>(emptyList())
    val prayers: StateFlow<List<PrayerElementDomain>> = _prayers

    private val _dynamicSongKey = MutableStateFlow<String?>(null)
    val dynamicSongKey: StateFlow<String?> = _dynamicSongKey.asStateFlow()

    private val _requestReview = MutableSharedFlow<Unit>()
    val requestReview = _requestReview.asSharedFlow()

    init {
        // Observe language from SettingsViewModel and trigger translation loading
        viewModelScope.launch {
            selectedLanguage.collect { language ->
                // When the language changes (from DataStore), load translations
                loadTranslations(language)
            }
        }
        viewModelScope.launch {
            prayers.collect {
                if (_dynamicSongKey.value == null) {
                    _dynamicSongKey.value = getSongKeyPriorityUseCase()
                }
            }
        }
    }

    private fun loadTranslations(language: AppLanguage) {
        viewModelScope.launch {
            val loadedTranslations = loadTranslationsUseCase(language)
            _translations.update { loadedTranslations }
        }
    }

    fun loadPrayerElements(
        filename: String,
        passedLanguage: AppLanguage? = null,
    ) {
        viewModelScope.launch {
            // Launch in ViewModelScope for async operation
            try {
                // Access the current language from SettingsViewModel
                val language = passedLanguage ?: selectedLanguage.value
                val prayers = getPrayerScreenContentUseCase(filename, language)
                _prayers.value = prayers
            } catch (e: Exception) {
                _prayers.value = listOf(PrayerElementDomain.Error(e.message ?: "Unknown error"))
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
        analyticsService.logPrayNowItemSelection(prayerName, prayerId)
    }

    fun reportError(
        errorMessage: String,
        errorLocation: String,
    ) {
        analyticsService.logError(errorMessage, errorLocation)
    }

    fun onPrayerScreenOpened() {
        viewModelScope.launch {
            inAppReviewManager.incrementAndGetPrayerScreenVisits()
        }
    }

    fun onSectionScreenOpened() {
        viewModelScope.launch {
            // This is safe to call every time. The manager handles the logic.
//            inAppReviewManager.checkForReview(activity)
            _requestReview.emit(Unit)
        }
    }
}
