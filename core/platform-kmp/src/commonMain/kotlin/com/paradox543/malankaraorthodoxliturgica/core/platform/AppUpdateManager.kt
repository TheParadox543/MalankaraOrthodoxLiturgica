package com.paradox543.malankaraorthodoxliturgica.core.platform

import com.paradox543.malankaraorthodoxliturgica.core.platform.model.AppUpdateInfo
import com.paradox543.malankaraorthodoxliturgica.core.platform.model.UpdateType
import kotlinx.coroutines.flow.StateFlow

interface AppUpdateManager {
    /**
     * Emits true when an update has been downloaded and is ready to install.
     * (Flexible update equivalent)
     */
    val updateReady: StateFlow<Boolean>

    /**
     * Checks if an update is available and prepares update flow.
     * Should be called when app comes to foreground.
     */
    suspend fun checkForUpdate(): AppUpdateInfo?

    /**
     * Starts the update flow.
     */
    fun startUpdate(updateType: UpdateType)

    /**
     * Completes an already downloaded update (if supported).
     */
    fun completeUpdate()

    /**
     * Optional lifecycle hook.
     * Called when app enters foreground.
     */
    fun onResume()

    /**
     * Optional lifecycle hook.
     * Called when app goes to background.
     */
    fun onPause()
}