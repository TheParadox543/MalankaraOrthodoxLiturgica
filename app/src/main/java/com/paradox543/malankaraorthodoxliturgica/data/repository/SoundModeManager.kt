package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.app.NotificationManager
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.media.AudioManager
import android.util.Log
import androidx.core.content.edit
import com.paradox543.malankaraorthodoxliturgica.data.model.SoundMode

object SoundModeManager {
    private const val PREF_NAME = "sound_mode_prefs"
    private const val KEY_PREVIOUS_MODE = "previous_interruption_filter"

    private var previousInterruptionFilter: Boolean? = null

    fun savePreviousState(
        context: Context,
        wasMuted: Boolean,
    ) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_PREVIOUS_MODE, wasMuted) }
    }

    fun loadPreviousState(context: Context): Boolean? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return if (prefs.contains(KEY_PREVIOUS_MODE)) prefs.getBoolean(KEY_PREVIOUS_MODE, false) else null
    }

    fun clearPreviousState(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit { remove(KEY_PREVIOUS_MODE) }
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
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
        Log.d(
            "SoundModeManager",
            "Applied notif settings: DND=${notificationManager.currentInterruptionFilter}, Silent=${audioManager.ringerMode}",
        )

        // Return if no permissions given
        if (!hasGrantedDndPermission(notificationManager)) return

        if (previousInterruptionFilter == null) {
            previousInterruptionFilter = loadPreviousState(context)
        }
        if (previousInterruptionFilter == null) {
            previousInterruptionFilter =
                notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL ||
                audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL
            Log.d("SoundModeManager", "Saved previousInterruptionFilter: $previousInterruptionFilter")
            val passedFilterState = if (previousInterruptionFilter == null) false else previousInterruptionFilter!!
            savePreviousState(context, passedFilterState)
        }
        if (previousInterruptionFilter != true) {
            Log.d("SoundModeManager", "Applying sound mode changes for mode: $soundMode, active: $active")
            when (soundMode) {
                SoundMode.OFF -> {
                    setSilentMode(false, audioManager)
                    setDndMode(false, notificationManager)
                }
                SoundMode.SILENT -> setSilentMode(active, audioManager)
                SoundMode.DND -> setDndMode(active, notificationManager)
            }
        } else {
            Log.d("SoundModeManager", "Not applying sound mode changes as previousInterruptionFilter is true")
        }
    }
}
