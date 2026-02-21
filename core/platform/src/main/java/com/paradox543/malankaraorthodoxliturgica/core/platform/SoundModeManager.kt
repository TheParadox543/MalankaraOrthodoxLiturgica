package com.paradox543.malankaraorthodoxliturgica.core.platform

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode

/**
 * Abstraction over device sound/DND management.
 *
 * The concrete implementation ([SoundModeManager] + [SoundModeService] in :app) handles
 * AudioManager, NotificationManager, and WorkManager scheduling.
 */
interface SoundModeManager {
    /** Returns whether the app has been granted Do-Not-Disturb policy access. */
    fun checkDndPermission(): Boolean

    /**
     * Applies the user's preferred [SoundMode] to the device.
     * Should be called whenever the setting changes (e.g. from a LaunchedEffect in MainActivity).
     */
    fun apply(mode: SoundMode)

    /**
     * Restores the device's original sound state if the app modified it this session.
     * Should be called in `onPause` / `onDestroy`.
     */
    fun restoreIfNeeded()

    /**
     * Schedules a WorkManager task to restore the sound state after [delayMinutes] minutes,
     * for cases where the app is backgrounded rather than explicitly closed.
     */
    fun scheduleRestore(delayMinutes: Int)

    /** Cancels any pending sound-restore work. Should be called when returning to foreground. */
    fun cancelRestoreWork()
}
