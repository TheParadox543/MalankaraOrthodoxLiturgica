package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedState
import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
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
//        if (!granted) {
//            _soundMode.value = SoundMode.OFF
//        } else {
//            viewModelScope.launch {
//                _soundMode.value = settingsRepository.getSoundMode()
//            }
//        }
    }

    fun logScreensVisited(
        routePattern: String,
        arguments: SavedState?,
    ) {
        val screenName: String
        val screenClass: String

        when {
            // Case 1: "section/{route}"
            routePattern.startsWith("section/") && routePattern.contains("{route}") -> {
                val sectionRouteValue = arguments?.getString("route")
                screenName = if (sectionRouteValue != null) "section/$sectionRouteValue" else routePattern
                screenClass = "SectionScreen"
            }
            // Case 2: "prayerScreen/{route}
            routePattern.startsWith("prayerScreen/") && routePattern.contains("{route}") -> {
                val prayerRouteValue = arguments?.getString("route")
                screenName = if (prayerRouteValue != null) "prayerScreen/$prayerRouteValue" else routePattern
                screenClass = "PrayerScreen"
            }

            // Case 3: "bible/{bookName}"
            routePattern.startsWith("bible/") && routePattern.contains("{bookName}") && !routePattern.contains("{chapterIndex}") -> {
                val bookName = arguments?.getString("bookName")
                screenName = if (bookName != null) "bible/$bookName" else routePattern
                screenClass = "BibleBookScreen"
            }
            // Case 4: "bible/{bookIndex}/{chapterIndex}"
            routePattern.startsWith("bible/") && routePattern.contains("{bookIndex}") && routePattern.contains("{chapterIndex}") -> {
                val bookIndex = arguments?.getString("bookIndex")
                val chapterIndex = arguments?.getString("chapterIndex")
                screenName =
                    when {
                        bookIndex != null && chapterIndex != null -> "bible/$bookIndex/$chapterIndex"
                        bookIndex != null -> "bible/$bookIndex" // Fallback if chapterIndex is missing but bookIndex isn't
                        else -> routePattern
                    }
                screenClass = "BibleChapterScreen"
            }
            // Default Case: For static routes or other unhandled patterns
            else -> {
                screenName = routePattern
                screenClass = "StaticScreen" // Or "ComposeScreen"
            }
        }

        val bundle =
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
            }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
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

fun requestDndPermission(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (!notificationManager.isNotificationPolicyAccessGranted) {
        Toast
            .makeText(
                context,
                "Please grant the app access to modify DND in settings.",
                Toast.LENGTH_LONG,
            ).show()
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        context.startActivity(intent)
    }
}
