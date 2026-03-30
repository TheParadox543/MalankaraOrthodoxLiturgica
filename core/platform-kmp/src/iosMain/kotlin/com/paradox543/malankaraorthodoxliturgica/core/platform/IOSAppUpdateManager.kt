package com.paradox543.malankaraorthodoxliturgica.core.platform

import com.paradox543.malankaraorthodoxliturgica.core.platform.model.AppUpdateInfo
import com.paradox543.malankaraorthodoxliturgica.core.platform.model.UpdateType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class IOSAppUpdateManager : AppUpdateManager {
    private val _updateReady = MutableStateFlow(false)
    override val updateReady: StateFlow<Boolean> = _updateReady

    override suspend fun checkForUpdate(): AppUpdateInfo? {
        // TODO call iTunes lookup API if you want real version check

        return null
    }

    override fun startUpdate(updateType: UpdateType) {
        // Open App Store page
        openAppStore()
    }

    override fun completeUpdate() {
        // Not applicable on iOS
    }

    override fun onResume() {
        // No-op
    }

    override fun onPause() {
        // No-op
    }

    private fun openAppStore() {
        // expect/actual OR UIKit call
    }
}