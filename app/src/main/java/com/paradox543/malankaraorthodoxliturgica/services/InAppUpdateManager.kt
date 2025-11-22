package com.paradox543.malankaraorthodoxliturgica.services

import android.app.Activity
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppUpdateManager @Inject constructor(
    private val appUpdateManager: AppUpdateManager,
) {
    companion object {
        const val UPDATE_REQUEST_CODE = 123
        private const val TAG = "InAppUpdateManager"
    }

    // State for managing Flexible updates.
    private val _updateDownloaded = MutableStateFlow(false)
    val updateDownloaded = _updateDownloaded.asStateFlow()

    private var updateListener: InstallStateUpdatedListener? = null

    /**
     * Checks for an available update and initiates the flexible update flow if one is found.
     * This should be called from the main activity's onResume() or onCreate().
     */
    fun checkForUpdate(activity: Activity) {
        Log.d(TAG, "Checking for updates...")
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val availability = appUpdateInfo.updateAvailability()
            Log.d(TAG, "Update availability: ${availabilityToString(availability)}")

            // First, check if an update is available at all.
            if (availability == UpdateAvailability.UPDATE_AVAILABLE) {
                val priority = appUpdateInfo.updatePriority()
                Log.d(TAG, "Update available with priority: $priority")

                when {
                    // For high-priority updates (3-5), use the IMMEDIATE flow.
                    priority >= 3 && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                        Log.d(TAG, "High priority update found. Starting IMMEDIATE flow.")
                        startImmediateUpdate(appUpdateInfo, activity)
                    }
                    // For medium-priority updates (0-2), use the FLEXIBLE flow.
                    priority >= 0 && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                        Log.d(TAG, "Medium priority update found. Starting FLEXIBLE flow.")
                        startFlexibleUpdate(appUpdateInfo, activity)
                    }
//                    // For low-priority updates (0-1), do nothing.
//                    else -> {
//                        Log.d(TAG, "Low priority update ($priority). Not prompting user.")
//                    }
                }
            } else {
                Log.d(TAG, "No update available.")
            }
        }

        appUpdateInfoTask.addOnFailureListener { e ->
            Log.e(TAG, "Failed to check for updates.", e)
        }
    }

    /**
     * Starts the immediate update
     */
    private fun startImmediateUpdate(
        appUpdateInfo: AppUpdateInfo,
        activity: Activity,
    ) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            activity,
            UPDATE_REQUEST_CODE,
        )
    }

    /**
     * Starts the flexible update flow.
     */
    private fun startFlexibleUpdate(
        appUpdateInfo: AppUpdateInfo,
        activity: Activity,
    ) {
        updateListener =
            InstallStateUpdatedListener { state ->
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    // An update has been downloaded. Notify the UI by updating the state.
                    _updateDownloaded.value = true
                    // Unregister the listener as it's no longer needed for this session.
                    unregisterListener()
                }
            }
        appUpdateManager.registerListener(updateListener!!)
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            activity,
            UPDATE_REQUEST_CODE,
        )
    }

    /**
     * Triggers the installation of the downloaded update.
     * This should be called by the UI when the user confirms the restart.
     */
    fun completeUpdate() {
        Log.d(TAG, "Completing update (FLEXIBLE flow)...")
        appUpdateManager.completeUpdate()
    }

    /**
     * Unregisters the update listener. Should be called in onPause().
     */
    fun unregisterListener() {
        updateListener?.let {
            Log.d(TAG, "Unregistering update listener (FLEXIBLE flow).")
            appUpdateManager.unregisterListener(it)
            updateListener = null
        }
    }

    /**
     * Resumes the update flow. Should be called in onResume() to handle updates
     * that were initiated but not completed.
     */
    fun resumeUpdate() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    Log.d(TAG, "Resumed and found downloaded update (FLEXIBLE flow).")
                    _updateDownloaded.value = true
                }
            }
    }

    private fun availabilityToString(availability: Int): String =
        when (availability) {
            UpdateAvailability.UPDATE_AVAILABLE -> "UPDATE_AVAILABLE"
            UpdateAvailability.UPDATE_NOT_AVAILABLE -> "UPDATE_NOT_AVAILABLE"
            UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> "DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS"
            UpdateAvailability.UNKNOWN -> "UNKNOWN"
            else -> "UNEXPECTED_VALUE"
        }
}