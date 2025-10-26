package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.app.NotificationManager
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.media.AudioManager
import android.util.Log
import com.paradox543.malankaraorthodoxliturgica.data.model.SoundMode

object SoundModeManager {
    private var previousInterruptionFilter: Boolean? = null

    fun hasGrantedDndPermission(notificationManager: NotificationManager): Boolean = notificationManager.isNotificationPolicyAccessGranted

    fun setDndMode(
        enable: Boolean,
        notificationManager: NotificationManager,
    ) {
        notificationManager.setInterruptionFilter(
            when (enable) {
                true -> NotificationManager.INTERRUPTION_FILTER_NONE
                false -> NotificationManager.INTERRUPTION_FILTER_ALL
            },
        )
    }

    fun setSilentMode(
        enable: Boolean,
        audioManager: AudioManager,
    ) {
        audioManager.ringerMode =
            when (enable) {
                true -> AudioManager.RINGER_MODE_SILENT
                false -> AudioManager.RINGER_MODE_NORMAL
            }
    }

    fun applyAppSoundMode(
        context: Context,
        soundMode: SoundMode,
        active: Boolean,
    ) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        Log.d(
            "SoundModeManager",
            "Applied notif settings: DND=${notificationManager.currentInterruptionFilter}, Silent=${audioManager.ringerMode}",
        )

        // Return if no permissions given
        if (!hasGrantedDndPermission(notificationManager)) return

        if (previousInterruptionFilter == null) {
            previousInterruptionFilter =
                notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL ||
                audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL
            Log.d("SoundModeManager", "Saved previousInterruptionFilter: $previousInterruptionFilter")
        }
        if (previousInterruptionFilter == false) {
            Log.d("SoundModeManager", "Applying sound mode changes for mode: $soundMode, active: $active")
            when (soundMode) {
                SoundMode.OFF -> {}
                SoundMode.SILENT -> setSilentMode(active, audioManager)
                SoundMode.DND -> setDndMode(active, notificationManager)
            }
        } else {
            Log.d("SoundModeManager", "Not applying sound mode changes as previousInterruptionFilter is true")
        }
    }
}
