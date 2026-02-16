package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.services.sound.SoundModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val soundModeManager: SoundModeManager,
) : ViewModel() {
    val selectedLanguage: StateFlow<AppLanguage> =
        settingsRepository.language
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppLanguage.MALAYALAM,
            )

    val onboardingCompleted: StateFlow<Boolean> =
        settingsRepository.onboardingCompleted
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false,
            )

    val fontScale: StateFlow<AppFontScale> =
        settingsRepository.fontScale
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AppFontScale.Medium,
            )

    val songScrollState: StateFlow<Boolean> =
        settingsRepository.songScrollState
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false,
            )

    val soundMode: StateFlow<SoundMode> =
        settingsRepository.soundMode
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SoundMode.OFF,
            )

    val soundRestoreDelay: StateFlow<Int> =
        settingsRepository.soundRestoreDelay
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 30,
            )

    private val _hasDndPermission = MutableStateFlow(false)
    val hasDndPermission = _hasDndPermission.asStateFlow()

    private val _shareApp = MutableSharedFlow<Unit>()
    val shareApp = _shareApp.asSharedFlow()

    // Internal MutableStateFlow to track AppFontSize changes for debounced saving
//    private val _debouncedAppFontScale = MutableStateFlow(AppFontScale.Medium)

    // Debounce state
    private var debounceJob: Job? = null

    init {
        // Debounce mechanism: only save to DataStore after a short delay of no new updates
//        viewModelScope.launch {
//            _debouncedAppFontScale.collectLatest { fontScaleToSave ->
//                delay(500L) // Wait for 500ms for more gesture events to stop
//                settingsRepository.setFontScale(fontScaleToSave) // Then save the enum
//            }
//        }
    }

    // Function to set (and save) language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            val bundle =
                Bundle().apply {
                    putString("language", language.name)
                }
            firebaseAnalytics.logEvent("language_selected", bundle)
        }
    }

    // Function to set (and save) font size
    fun setFontScaleFromSettings(scale: AppFontScale) {
        viewModelScope.launch {
            settingsRepository.setFontScale(scale) // Convert TextUnit back to Int for DataStore
        }
    }

    fun setFontScaleDebounced(direction: Int) {
//        if (direction > 0) {
//            _selectedAppFontScale.value = _selectedAppFontScale.value.next()
//        } else if (direction < 0) {
//            _selectedAppFontScale.value = _selectedAppFontScale.value.prev()
//        }
//        updateFontScaleWithDebounce(_selectedAppFontScale.value)
    }

    fun updateFontScaleWithDebounce(newScale: AppFontScale) {
//        _selectedAppFontScale.value = newScale

        debounceJob?.cancel()
        debounceJob =
            viewModelScope.launch {
                delay(300) // Example debounce time
                settingsRepository.setFontScale(newScale)
            }
    }

    fun logTutorialStart() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
    }

    fun setOnboardingCompleted(completed: Boolean = true) {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(completed)
            if (completed) {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
            }
        }
    }

    fun setSongScrollState(isHorizontal: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSongScrollState(isHorizontal)
        }
    }

    fun refreshDndPermissionStatus() {
        val granted = soundModeManager.checkDndPermission()
        setDndPermissionStatus(granted)
    }

    fun setSoundMode(permissionState: SoundMode) {
        viewModelScope.launch {
            settingsRepository.setSoundMode(permissionState)
        }
    }

    fun setSoundRestoreDelay(delay: Int) {
        viewModelScope.launch {
            settingsRepository.setSoundRestoreDelay(delay)
        }
    }

    fun setDndPermissionStatus(granted: Boolean) {
        _hasDndPermission.value = granted
    }

    fun onShareAppClicked() {
        viewModelScope.launch { _shareApp.emit(Unit) }
    }
}
