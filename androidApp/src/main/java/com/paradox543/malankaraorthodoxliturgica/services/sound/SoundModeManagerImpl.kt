package com.paradox543.malankaraorthodoxliturgica.services.sound

import android.content.Context
import androidx.core.content.edit
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import java.util.concurrent.TimeUnit
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager as SoundModeManagerInterface

class SoundModeManagerImpl(
    context: Context,
    private val soundModeService: SoundModeService,
    private val workManager: WorkManager,
) : SoundModeManagerInterface {
    private val prefs = context.getSharedPreferences("sound_mode_prefs", Context.MODE_PRIVATE)

    // Tracks whether the app changed the sound. Persisted for WorkManager.
    private var isSoundModified: Boolean
        get() = prefs.getBoolean("is_sound_modified", false)
        set(value) = prefs.edit { putBoolean("is_sound_modified", value) }

    // Tracks what the user actually selected in the app.
    private var requestedMode: SoundMode
        get() = SoundMode.entries[prefs.getInt("requested_mode", SoundMode.OFF.ordinal)]
        set(value) = prefs.edit { putInt("requested_mode", value.ordinal) }

    override fun checkDndPermission(): Boolean = soundModeService.hasDndPermission()

    // -------------------------------------------------------------------------
    // APPLY USER PREFERENCE
    // Called when soundMode changes (LaunchedEffect in MainActivity)
    // -------------------------------------------------------------------------
    override fun apply(mode: SoundMode) {
        requestedMode = mode
        val modified = soundModeService.applyUserPreference(mode, isSoundModified)
        isSoundModified = modified
    }

    // -------------------------------------------------------------------------
    // RESTORE SOUND IF NEEDED
    // Called in onPause/onDestroy
    // -------------------------------------------------------------------------
    override fun restoreIfNeeded() {
        if (isSoundModified) {
            soundModeService.restoreToNormal()
            isSoundModified = false
        }
    }

    // -------------------------------------------------------------------------
    // SCHEDULE RESTORATION (WorkManager)
    // Called when app goes to background
    // -------------------------------------------------------------------------
    override fun scheduleRestore(delayMinutes: Int) {
        if (!isSoundModified) {
            // If user turned on a mode but we didn't change anything (already silent), show info
            if (requestedMode != SoundMode.OFF) {
                soundModeService.showNoChangeToast()
            }
            return
        }

        soundModeService.showRestoreToast(delayMinutes)

        val work =
            OneTimeWorkRequestBuilder<RestoreSoundWorker>()
                .setInitialDelay(delayMinutes.toLong(), TimeUnit.MINUTES)
                .build()

        workManager.enqueueUniqueWork(
            "restore_sound_mode",
            ExistingWorkPolicy.REPLACE,
            work,
        )
    }

    override fun cancelRestoreWork() {
        workManager.cancelUniqueWork("restore_sound_mode")
    }
}
