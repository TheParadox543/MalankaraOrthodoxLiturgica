package com.paradox543.malankaraorthodoxliturgica.services

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.util.Log
import com.paradox543.malankaraorthodoxliturgica.domain.model.SoundMode

object SoundModeManager {
    fun checkPreviousFilterState(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL ||
            audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL
    }

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
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        Log.d(
            "SoundModeManager",
            "Applied notif settings: DND=${notificationManager.currentInterruptionFilter}, Silent=${audioManager.ringerMode}",
        )

        // Return if no permissions given
        if (!hasGrantedDndPermission(notificationManager)) return
        Log.d("SoundModeManager", "Applying sound mode changes for mode: $soundMode, active: $active")
        when (soundMode) {
            SoundMode.OFF -> {
                setSilentMode(false, audioManager)
                setDndMode(false, notificationManager)
            }

            SoundMode.SILENT -> {
                setSilentMode(active, audioManager)
            }

            SoundMode.DND -> {
                setDndMode(active, notificationManager)
            }
        }
    }
}