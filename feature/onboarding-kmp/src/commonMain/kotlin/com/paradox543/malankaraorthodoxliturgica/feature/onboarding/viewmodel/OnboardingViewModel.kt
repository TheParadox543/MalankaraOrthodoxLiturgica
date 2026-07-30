package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeCapability
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerScreenContentUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.OnboardingStage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val settingsRepository: SettingsRepository,
    private val analyticsService: AnalyticsService,
    private val getPrayerScreenContentUseCase: GetPrayerScreenContentUseCase,
    private val appInfoProvider: AppInfoProvider,
    private val soundModeCapability: SoundModeCapability,
) : ViewModel() {
    val onboardingStage: StateFlow<OnboardingStage> =
        settingsRepository.onboardingStage
            .map { stageInt ->
                val stage = OnboardingStage.fromInt(stageInt)
                if (stage == OnboardingStage.SOUND_MODE && !showSoundModePage) {
                    OnboardingStage.SONG_WRAP
                } else {
                    stage
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = OnboardingStage.WELCOME,
            )

    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.MALAYALAM,
        )

    val fontScale: StateFlow<AppFontScale> =
        settingsRepository.fontScale.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppFontScale.Medium,
        )

    val songScrollState: StateFlow<Boolean> =
        settingsRepository.songScrollState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    val soundMode: StateFlow<SoundMode> =
        settingsRepository.soundMode.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SoundMode.OFF,
        )

    val soundRestoreDelay: StateFlow<Int> =
        settingsRepository.soundRestoreDelay.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 30,
        )

    private val _hasDndPermission = MutableStateFlow(false)
    val hasDndPermission = _hasDndPermission.asStateFlow()

    private val _prayers = MutableStateFlow<List<PrayerElement>>(emptyList())
    val prayers: StateFlow<List<PrayerElement>> = _prayers

    val version = appInfoProvider.versionName
    val showSoundModePage = soundModeCapability.isAvailable

    init {
        refreshDndPermissionStatus()
    }

    fun nextPage() {
        val current = onboardingStage.value
        val next =
            when (current) {
                OnboardingStage.WELCOME -> OnboardingStage.SONG_WRAP
                OnboardingStage.SONG_WRAP -> {
                    if (showSoundModePage) OnboardingStage.SOUND_MODE
                    else OnboardingStage.COMPLETE
                }
                OnboardingStage.SOUND_MODE -> OnboardingStage.COMPLETE
                OnboardingStage.COMPLETE -> OnboardingStage.COMPLETE
            }
        if (next != current) {
            setOnboardingStage(next.value)
        }
    }

    fun previousPage() {
        val current = onboardingStage.value
        val previous =
            when (current) {
                OnboardingStage.WELCOME -> OnboardingStage.WELCOME
                OnboardingStage.SONG_WRAP -> OnboardingStage.WELCOME
                OnboardingStage.SOUND_MODE -> OnboardingStage.SONG_WRAP
                OnboardingStage.COMPLETE -> {
                    if (showSoundModePage) OnboardingStage.SOUND_MODE
                    else OnboardingStage.SONG_WRAP
                }
            }
        if (previous != current) {
            setOnboardingStage(previous.value)
        }
    }

    fun skipOnboarding() {
        setOnboardingStage(OnboardingStage.COMPLETE.value)
    }

    fun finishOnboarding() {
        setOnboardingStage(OnboardingStage.COMPLETE.value)
    }

    private fun setOnboardingStage(stage: Int) {
        viewModelScope.launch {
            settingsRepository.setOnboardingStage(stage)
            if (stage == OnboardingStage.COMPLETE.value) {
                analyticsService.logEvent(AnalyticsEvent.TutorialCompleted)
            }
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
                val language: AppLanguage = passedLanguage ?: selectedLanguage.value
                val prayers = getPrayerScreenContentUseCase(filename, language)
                _prayers.value = prayers
            } catch (e: Exception) {
                _prayers.value = listOf(PrayerElement.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun logTutorialStart() {
        analyticsService.logEvent(AnalyticsEvent.TutorialStarted)
    }

    // Function to set (and save) language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            analyticsService.logEvent(AnalyticsEvent.LanguageSelected(language.name))
        }
    }

    // Function to set (and save) font size
    fun setFontScaleFromSettings(scale: AppFontScale) {
        viewModelScope.launch {
            settingsRepository.setFontScale(scale) // Convert TextUnit back to Int for DataStore
        }
    }

    fun setSongScrollState(isHorizontal: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSongScrollState(isHorizontal)
        }
    }

    fun setSoundMode(mode: SoundMode) {
        viewModelScope.launch {
            settingsRepository.setSoundMode(mode)
        }
    }

    fun setSoundRestoreDelay(delay: Int) {
        viewModelScope.launch {
            settingsRepository.setSoundRestoreDelay(delay)
        }
    }

    fun refreshDndPermissionStatus() {
        _hasDndPermission.value = soundModeCapability.hasPermission
    }
}
