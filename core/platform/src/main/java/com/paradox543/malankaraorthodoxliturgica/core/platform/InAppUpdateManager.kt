package com.paradox543.malankaraorthodoxliturgica.core.platform

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction over the Google Play In-App Update API.
 *
 * The concrete implementation lives in :app.
 */
interface InAppUpdateManager {
    /**
     * Emits `true` once a flexible update has been fully downloaded and is
     * ready to install.
     */
    val updateDownloaded: StateFlow<Boolean>

    /**
     * Checks for an available update and starts the appropriate update flow
     * (IMMEDIATE for high-priority, FLEXIBLE otherwise).
     *
     * Should be called from the main activity's `onCreate` / `onResume`.
     */
    fun checkForUpdate(activity: Activity)

    /**
     * Triggers the installation of a downloaded flexible update.
     * Should be called when the user confirms the restart prompt.
     */
    fun completeUpdate()

    /**
     * Unregisters the flexible-update install listener.
     * Should be called in `onPause`.
     */
    fun unregisterListener()

    /**
     * Re-checks update state after returning to the foreground.
     * Should be called in `onResume` to surface updates downloaded in the background.
     */
    fun resumeUpdate()
}
