package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paradox543.malankaraorthodoxliturgica.data.model.SoundMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RestoreSoundWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        try {
            SoundModeManager.applyAppSoundMode(applicationContext, SoundMode.OFF, false)
            SoundModeManager.clearPreviousState(applicationContext)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}
