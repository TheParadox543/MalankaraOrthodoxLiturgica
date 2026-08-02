package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.Synchronizer
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.ui.StartupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

class StartupViewModel(
    private val settingsRepository: SettingsRepository,
    private val synchronizer: Synchronizer,
) : ViewModel() {
    private val _startupState = MutableStateFlow<StartupState>(StartupState.Loading)
    val startupState = _startupState.asStateFlow()

    init {
        viewModelScope.launch {
            // 1. Trigger synchronization
            // We set a timeout to ensure startup isn't blocked forever if sync hangs
            withTimeoutOrNull(10000.milliseconds) {
                synchronizer.synchronize()
            }

            // 2. Load onboarding stage
            val onboardingStage = settingsRepository.onboardingStage.first()

            _startupState.value =
                StartupState.Ready(
                    onboardingStage = onboardingStage,
                )
        }
    }
}
