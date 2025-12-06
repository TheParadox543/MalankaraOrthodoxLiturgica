package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.services.sound.SoundModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val soundModeManager: SoundModeManager,
) : ViewModel() {
    val selectedLanguage = settingsRepository.language
    val onboardingCompleted = settingsRepository.onboardingCompleted
    val fontScale = settingsRepository.fontScale
    val songScrollState = settingsRepository.songScrollState
    val soundMode = settingsRepository.soundMode
    val soundRestoreDelay = settingsRepository.soundRestoreDelay

    private val _hasDndPermission = MutableStateFlow(false)
    val hasDndPermission = _hasDndPermission.asStateFlow()

    // Internal MutableStateFlow to track AppFontSize changes for debounced saving
    private val _debouncedAppFontScale = MutableStateFlow(AppFontScale.Medium)

    // Debounce state
    private var debounceJob: Job? = null

    init {
        // Debounce mechanism: only save to DataStore after a short delay of no new updates
        viewModelScope.launch {
            _debouncedAppFontScale.collectLatest { fontScaleToSave ->
                delay(500L) // Wait for 500ms for more gesture events to stop
                settingsRepository.setFontScale(fontScaleToSave) // Then save the enum
            }
        }
    }

    // Function to set (and save) language
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            val bundle =
                Bundle().apply {
                    putString("language", language.name)
                }
            firebaseAnalytics.logEvent("language_selected", bundle)
        }
    }

    // Function to set (and save) font size
    fun setFontScaleFromSettings(scale: AppFontScale) {
        viewModelScope.launch {
            settingsRepository.setFontScale(scale) // Convert TextUnit back to Int for DataStore
        }
    }

    fun setFontScaleDebounced(direction: Int) {
//        if (direction > 0) {
//            _selectedAppFontScale.value = _selectedAppFontScale.value.next()
//        } else if (direction < 0) {
//            _selectedAppFontScale.value = _selectedAppFontScale.value.prev()
//        }
//        updateFontScaleWithDebounce(_selectedAppFontScale.value)
    }

    fun updateFontScaleWithDebounce(newScale: AppFontScale) {
//        _selectedAppFontScale.value = newScale

        debounceJob?.cancel()
        debounceJob =
            viewModelScope.launch {
                delay(300) // Example debounce time
                settingsRepository.setFontScale(newScale)
            }
    }

    fun logTutorialStart() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null)
    }

    fun setOnboardingCompleted(completed: Boolean = true) {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(completed)
            if (completed) {
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
            }
        }
    }

    fun setSongScrollState(isHorizontal: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSongScrollState(isHorizontal)
        }
    }

    fun refreshDndPermissionStatus() {
        val granted = soundModeManager.checkDndPermission()
        setDndPermissionStatus(granted)
    }

    fun setSoundMode(permissionState: SoundMode) {
        viewModelScope.launch {
            settingsRepository.setSoundMode(permissionState)
        }
    }

    fun setSoundRestoreDelay(delay: Int) {
        viewModelScope.launch {
            settingsRepository.setSoundRestoreDelay(delay)
        }
    }

    fun setDndPermissionStatus(granted: Boolean) {
        _hasDndPermission.value = granted
    }

    /**
     * Launches an Android share intent to share the app's Play Store link.
     * @param shareMessage An optional custom message to include.
     * @param appPackageName Your app's package name.
     */
    fun shareAppPlayStoreLink(
        context: Context,
        shareMessage: String = "",
        appPackageName: String? = null,
    ) {
        val appPackageName = appPackageName ?: "com.paradox543.malankaraorthodoxliturgica"
        val playStoreLink = "https://play.google.com/store/apps/details?id=$appPackageName"

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain" // We are sharing plain text
                putExtra(Intent.EXTRA_SUBJECT, "Check out this amazing app!") // Subject for email/other apps
                putExtra(
                    Intent.EXTRA_TEXT,
                    "$shareMessage\n$playStoreLink", // Your message + the Play Store link
                )
            }

        // Check if there's any app to handle this intent
        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(shareIntent, "Share App Via"))
            val bundle =
                Bundle().apply {
                    putString(FirebaseAnalytics.Param.CONTENT_TYPE, "share_app")
                    putString(FirebaseAnalytics.Param.ITEM_ID, "app_link")
                    putString(FirebaseAnalytics.Param.METHOD, "text/plain")
                }
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
        } else {
            // Optionally, show a toast or message if no app can handle the share intent
            // Toast.makeText(context, "No app found to share with.", Toast.LENGTH_SHORT).show()
        }
    }
}
