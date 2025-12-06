package com.paradox543.malankaraorthodoxliturgica.services.sound

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RestoreSoundWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val soundModeManager: SoundModeManager,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result =
        try {
            soundModeManager.restoreIfNeeded()   // <— CLEAN, no static calls
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
}