package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.domain.model.StartupState
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
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
            val language = settingsRepository.getInitialLanguage()
            val onboarding = settingsRepository.getInitialOnboardingCompleted()
            val fontScale = settingsRepository.fontScale.first()

            // preload tree OR any other critical init work here

            _startupState.value =
                StartupState.Ready(
                    language = language,
                    fontScale = fontScale,
                    onboardingCompleted = onboarding,
                )
        }
    }
}
