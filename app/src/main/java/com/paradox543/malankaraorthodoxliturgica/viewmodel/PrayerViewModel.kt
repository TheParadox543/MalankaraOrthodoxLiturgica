package com.paradox543.malankaraorthodoxliturgica.viewmodel

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.model.DataStoreManager
import com.paradox543.malankaraorthodoxliturgica.model.PrayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val app: Application,
    private val prayerRepository: PrayerRepository,
    private val dataStoreManager: DataStoreManager
) : AndroidViewModel(app) {

    private val _selectedLanguage = MutableStateFlow("ml")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _selectedFontSize = MutableStateFlow(16.sp) // Default to medium
    val selectedFontSize: StateFlow<TextUnit> = _selectedFontSize.asStateFlow()

    private val _translations = MutableStateFlow<Map<String, String>>(emptyMap())
    val translations: StateFlow<Map<String, String>> = _translations.asStateFlow()

//    private val _selectedNotificationPreference = MutableStateFlow("off")
//    val selectedNotificationPreference: StateFlow<String> = _selectedNotificationPreference.asStateFlow()
//
//    // Store the original interruption filter to restore it later
//    private var originalInterruptionFilter: Int? = null
//
//    // Get NotificationManager
//    private val notificationManager: NotificationManager =
//        app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        // Load stored language from DataStore
        viewModelScope.launch {
            dataStoreManager.selectedLanguage.collect { language ->
                _selectedLanguage.value = language
                loadTranslations(language)
            }
        }
        viewModelScope.launch {
            dataStoreManager.selectedFont.collect{ size ->
                _selectedFontSize.value = size.sp
            }
        }
//        viewModelScope.launch {
//            dataStoreManager.selectedNotificationPreference
//                .distinctUntilChanged()
//                .collect { preference ->
//                    _selectedNotificationPreference.value = preference
//                }
//        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch{
            dataStoreManager.saveLanguage(language)
        }
        _selectedLanguage.value = language
        loadTranslations(language)
    }

    fun setFontSize(size: TextUnit) {
        _selectedFontSize.value = size
        viewModelScope.launch {
            dataStoreManager.saveFontSize(size.value.toInt())
        }
    }

//    fun setNotificationPreference(preference: String) {
//        _selectedNotificationPreference.value = preference
//        viewModelScope.launch {
//            dataStoreManager.saveNotificationPreference(preference)
//        }
//    }

    private fun loadTranslations(language: String) {
        viewModelScope.launch {
            val loadedTranslations = prayerRepository.loadTranslations(language)
            _translations.update { loadedTranslations }
        }
    }

    private val _topBarNames = MutableStateFlow<List<String>>(emptyList())
    val topBarNames: StateFlow<List<String>> = _topBarNames

    fun setTopBarKeys(route: String) {
        _topBarNames.value = route.split("_")
    }

    private val _prayers = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val prayers: StateFlow<List<Map<String, Any>>> = _prayers

    fun loadPrayers(filename: String, language: String) {
        try {
            val prayers = prayerRepository.loadPrayers(filename, language)
            _prayers.value = prayers
        } catch (e: Exception) {
            throw e
        }
    }
//
//    /**
//     * Checks if the app has the necessary permission to change notification policy.
//     * @return True if permission is granted, false otherwise.
//     */
//    fun hasNotificationPolicyAccess(): Boolean {
//        return notificationManager.isNotificationPolicyAccessGranted
//    }
//
//    /**
//     * Attempts to change the device's notification mode based on user preference.
//     * Should be called when entering the PrayerScreen.
//     * Stores the original mode to restore later.
//     */
//    fun activatePrayerNotificationMode() = viewModelScope.launch {
//        if (!hasNotificationPolicyAccess()) {
//            Log.w("PrayerViewModel", "Notification policy access not granted. Cannot change mode.")
//            // You might want to expose a StateFlow to trigger a permission request dialog in the UI
//            return@launch
//        }
//
//        // Store the current mode before changing it
//        originalInterruptionFilter = notificationManager.currentInterruptionFilter
//        Log.d("PrayerViewModel", "Original notification filter: $originalInterruptionFilter")
//
//        when (_selectedNotificationPreference.value) {
//            "silent" -> {
//                Log.d("PrayerViewModel", "Activating silent mode.")
//                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
//            }
//            "dnd" -> {
//                Log.d("PrayerViewModel", "Activating DND mode.")
//                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
//            }
//            "off" -> {
//                // If user selected "off", we don't change anything, just ensure original is stored.
//                // This 'off' means 'don't change the system setting'.
//                Log.d("PrayerViewModel", "Notification preference is 'off'. Not changing system mode.")
//            }
//            else -> {
//                Log.w("PrayerViewModel", "Unknown notification preference: ${_selectedNotificationPreference.value}")
//            }
//        }
//    }
//
//    /**
//     * Restores the device's notification mode to its original setting.
//     * Should be called when exiting the PrayerScreen.
//     */
//    fun deactivatePrayerNotificationMode() {
//        if (originalInterruptionFilter != null && hasNotificationPolicyAccess()) {
//            Log.d("PrayerViewModel", "Restoring original notification filter: $originalInterruptionFilter")
//            notificationManager.setInterruptionFilter(originalInterruptionFilter!!)
//            originalInterruptionFilter = null // Clear the stored original mode
//        } else if (!hasNotificationPolicyAccess()) {
//            Log.w("PrayerViewModel", "Notification policy access not granted. Cannot restore mode.")
//        } else {
//            Log.d("PrayerViewModel", "No original notification filter to restore.")
//        }
//    }
//
//    /**
//     * Provides an Intent to open the Notification Policy Access settings page.
//     * Use this in your UI to prompt the user for permission.
//     */
//    fun getNotificationPolicyAccessIntent(): Intent {
//        return Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
//            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Important for launching from non-activity context
//        }
//    }
}

