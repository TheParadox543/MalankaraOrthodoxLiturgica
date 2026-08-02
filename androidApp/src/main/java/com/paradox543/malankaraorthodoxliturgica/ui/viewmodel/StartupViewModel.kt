package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.ContentSyncWorker
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.Synchronizer
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.ui.StartupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StartupViewModel(
    private val settingsRepository: SettingsRepository,
    private val synchronizer: Synchronizer,
    private val workManager: WorkManager,
) : ViewModel() {
    private val _startupState = MutableStateFlow<StartupState>(StartupState.Loading)
    val startupState = _startupState.asStateFlow()

    init {
        viewModelScope.launch {
            // 1. Enqueue background synchronization
            enqueueSync()

            // 2. Load onboarding stage
            val onboardingStage = settingsRepository.onboardingStage.first()

            _startupState.value =
                StartupState.Ready(
                    onboardingStage = onboardingStage,
                )
        }
    }

    private fun enqueueSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<ContentSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncRequest)
    }
}
