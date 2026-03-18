package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerScreenContentUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository,
    private val analyticsService: AnalyticsService,
    private val getPrayerScreenContentUseCase: GetPrayerScreenContentUseCase,
) : ViewModel() {
    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = runBlocking { settingsRepository.language.first() },
        )

    val fontScale: StateFlow<AppFontScale> =
        settingsRepository.fontScale.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppFontScale.Medium,
        )

    private val _prayers = MutableStateFlow<List<PrayerElement>>(emptyList())
    val prayers: StateFlow<List<PrayerElement>> = _prayers

    fun loadPrayerElements(
        filename: String,
        passedLanguage: AppLanguage? = null,
    ) {
        viewModelScope.launch {
            // Launch in ViewModelScope for async operation
            try {
                // Access the current language from SettingsViewModel
                val language: AppLanguage = passedLanguage ?: selectedLanguage.value
                val prayers = getPrayerScreenContentUseCase(filename, language)
                _prayers.value = prayers
            } catch (e: Exception) {
                _prayers.value = listOf(PrayerElement.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun logTutorialStart() {
        analyticsService.logTutorialStarted()
    }

    // Function to set (and save) language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            analyticsService.logLanguageSelected(language.name)
        }
    }

    // Function to set (and save) font size
    fun setFontScaleFromSettings(scale: AppFontScale) {
        viewModelScope.launch {
            settingsRepository.setFontScale(scale) // Convert TextUnit back to Int for DataStore
        }
    }

    fun setOnboardingCompleted(completed: Boolean = true) {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(completed)
            if (completed) {
                analyticsService.logTutorialCompleted()
            }
        }
    }
}