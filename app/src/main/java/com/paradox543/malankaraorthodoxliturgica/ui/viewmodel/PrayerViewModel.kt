package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.GetPrayerScreenContentUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.GetSongKeyPriorityUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.LoadTranslationsUseCase
import com.paradox543.malankaraorthodoxliturgica.services.InAppReviewManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    private val firebaseAnalytics: FirebaseAnalytics,
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
                // Consider more robust error handling (e.g., expose to UI via StateFlow)
//                throw e
                _prayers.value = listOf(PrayerElementDomain.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun setDynamicSongKey(key: String) {
        _dynamicSongKey.value = key
    }

    fun logPrayNowItemSelection(
        prayerName: String,
        prayerId: String,
    ) {
        val bundle =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, prayerId) // Use ITEM_ID for specific items
                putString(FirebaseAnalytics.Param.ITEM_NAME, prayerName)
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "prayNow") // Custom parameter
            }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun handlePrayerElementError(
        errorMessage: String,
        errorLocation: String,
    ) {
        val bundle =
            Bundle().apply {
                putString("error_description", errorMessage)
                putString("error_location", errorLocation) // Specific to this error source
            }
        firebaseAnalytics.logEvent("app_error", bundle)
    }

    fun onPrayerScreenOpened() {
        viewModelScope.launch {
            inAppReviewManager.incrementAndGetPrayerScreenVisits()
        }
    }

    fun onSectionScreenOpened(activity: Activity) {
        viewModelScope.launch {
            // This is safe to call every time. The manager handles the logic.
            inAppReviewManager.checkForReview(activity)
        }
    }
}
