package com.paradox543.malankaraorthodoxliturgica.services.sound

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SoundModeService @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Store the internal mode for this session
    private var currentMode: SoundModeInternal = SoundModeInternal.DISABLED

    // ---- PUBLIC API --------------------------------------------------------

    fun applyUserPreference(mode: SoundMode): Boolean {
        if (!hasDndPermission()) return false
        val internal = mapPreferenceToInternal(mode)
        currentMode = internal

        return when (internal) {
            SoundModeInternal.DISABLED -> {
                false
            }

            SoundModeInternal.SILENT -> {
                setSilent()
                true
            }

            SoundModeInternal.DND -> {
                setDnd()
                true
            }

            SoundModeInternal.NORMAL -> { // never triggered directly by user
                false
            }
        }
    }

    fun restoreIfNeeded() {
        // Only restore if we actually modified the sound
        if (currentMode == SoundModeInternal.SILENT ||
            currentMode == SoundModeInternal.DND
        ) {
            setNormal()
        }

        // Reset for safety
        currentMode = SoundModeInternal.DISABLED
    }

    fun hasDndPermission(): Boolean {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.isNotificationPolicyAccessGranted
    }

    // ---- INTERNAL MAPPING --------------------------------------------------

    private fun mapPreferenceToInternal(pref: SoundMode): SoundModeInternal =
        when (pref) {
            SoundMode.OFF -> SoundModeInternal.DISABLED
            SoundMode.SILENT -> SoundModeInternal.SILENT
            SoundMode.DND -> SoundModeInternal.DND
        }

    // ---- PLATFORM ACTIONS --------------------------------------------------

    private fun setSilent() {
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
    }

    private fun setDnd() {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            // You might want a callback to UI requesting permission
            return
        }
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
    }

    private fun setNormal() {
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }
}
