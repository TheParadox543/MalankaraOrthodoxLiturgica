package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.ui.StartupState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _startupState = MutableStateFlow<StartupState>(StartupState.Loading)
    val startupState = _startupState.asStateFlow()

    init {
        viewModelScope.launch {
            // Use drop(1).first() to skip the initial default value and wait for the actual DataStore value
            // Or better yet, collect once to ensure we get the real stored value
            val language = settingsRepository.language.first()
            val onboarding = settingsRepository.onboardingCompleted.first()
            val fontScale = settingsRepository.fontScale.first()
            val soundMode = settingsRepository.soundMode.first()

            // preload tree OR any other critical init work here

            _startupState.value =
                StartupState.Ready(
                    language = language,
                    fontScale = fontScale,
                    onboardingCompleted = onboarding,
                    soundMode = soundMode,
                )
        }
    }
}
