package com.paradox543.malankaraorthodoxliturgica.services.sound

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.widget.Toast
import com.paradox543.malankaraorthodoxliturgica.R
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode

class SoundModeService(
    private val context: Context,
) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Store the internal mode for this session
    private var currentMode: SoundModeInternal = SoundModeInternal.DISABLED

    // ---- PUBLIC API --------------------------------------------------------

    fun applyUserPreference(mode: SoundMode, previouslyModified: Boolean): Boolean {
        if (!hasDndPermission()) return false
        val internal = mapPreferenceToInternal(mode)

        // If we are turning it off, just reset currentMode and return false
        if (internal == SoundModeInternal.DISABLED) {
            currentMode = internal
            return false
        }

        // Check if device is already in a non-normal state AND we didn't cause it
        val wasAlreadyModified = !isCurrentlyNormal() && !previouslyModified

        currentMode = internal

        return when (internal) {
            SoundModeInternal.SILENT -> {
                if (!wasAlreadyModified) setSilent()
                !wasAlreadyModified
            }

            SoundModeInternal.DND -> {
                if (!wasAlreadyModified) setDnd()
                !wasAlreadyModified
            }

            else -> false
        }
    }

    fun restoreToNormal() {
        setNormal()
        currentMode = SoundModeInternal.DISABLED
    }

    fun showRestoreToast(delayMinutes: Int) {
        val message = context.getString(R.string.sound_restore_scheduled, delayMinutes)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showNoChangeToast() {
        Toast.makeText(context, R.string.sound_not_modified, Toast.LENGTH_SHORT).show()
    }

    fun isCurrentlyNormal(): Boolean {
        val ringerNormal = audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL
        val dndNormal = notificationManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_ALL
        return ringerNormal && dndNormal
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
