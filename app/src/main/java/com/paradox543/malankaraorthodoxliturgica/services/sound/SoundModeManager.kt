package com.paradox543.malankaraorthodoxliturgica.services.sound

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager as SoundModeManagerInterface
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundModeManager @Inject constructor(
    private val soundModeService: SoundModeService,
    private val workManager: WorkManager,
) : SoundModeManagerInterface {
    // Tracks whether the app changed the sound this session.
    private var isSoundModified = false

    override fun checkDndPermission(): Boolean = soundModeService.hasDndPermission()

    // -------------------------------------------------------------------------
    // APPLY USER PREFERENCE
    // Called when soundMode changes (LaunchedEffect in MainActivity)
    // -------------------------------------------------------------------------
    override fun apply(mode: SoundMode) {
        val modified = soundModeService.applyUserPreference(mode)
        isSoundModified = modified
    }

    // -------------------------------------------------------------------------
    // RESTORE SOUND IF NEEDED
    // Called in onPause/onDestroy
    // -------------------------------------------------------------------------
    override fun restoreIfNeeded() {
        if (isSoundModified) {
            soundModeService.restoreIfNeeded()
            isSoundModified = false
        }
    }

    // -------------------------------------------------------------------------
    // SCHEDULE RESTORATION (WorkManager)
    // Called when app goes to background
    // -------------------------------------------------------------------------
    override fun scheduleRestore(delayMinutes: Int) {
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
