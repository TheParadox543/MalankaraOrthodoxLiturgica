package com.paradox543.malankaraorthodoxliturgica.services.sound

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RestoreSoundWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams),
    KoinComponent {
    private val soundModeManager: SoundModeManager by inject()

    override suspend fun doWork(): Result =
        try {
            soundModeManager.restoreIfNeeded()   // <— CLEAN, no static calls
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
}