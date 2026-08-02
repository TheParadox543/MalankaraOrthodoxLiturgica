package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.Synchronizer
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ContentSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val synchronizer: Synchronizer by inject()

    override suspend fun doWork(): Result {
        AppLogger.d("ContentSyncWorker") { "Starting background content sync..." }
        return try {
            synchronizer.synchronize()
            Result.success()
        } catch (e: Exception) {
            AppLogger.e("ContentSyncWorker", e) { "Background sync failed." }
            Result.retry()
        }
    }
}
