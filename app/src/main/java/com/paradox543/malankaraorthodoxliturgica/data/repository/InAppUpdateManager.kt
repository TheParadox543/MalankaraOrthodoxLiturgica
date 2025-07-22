package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppUpdateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appUpdateManager: AppUpdateManager
) {

    companion object {
        const val UPDATE_REQUEST_CODE = 123
    }

    private var updateListener: InstallStateUpdatedListener? = null

    /**
     * Checks for an available update and initiates the flexible update flow if one is found.
     * This should be called from the main activity's onResume() or onCreate().
     */
    fun checkForUpdate(activity: Activity) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            // Check if an update is available and if a flexible update is allowed.
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE)
            ) {
                // An update is available, start the flexible update flow.
                startFlexibleUpdate(appUpdateInfo, activity)
            }
        }
    }

    /**
     * Starts the flexible update flow.
     */
    private fun startFlexibleUpdate(appUpdateInfo: AppUpdateInfo, activity: Activity) {
        // Create a listener to monitor the download and installation status.
        updateListener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                // The update has been downloaded. Show a notification to the user
                // to complete the update.
                Toast.makeText(
                    context,
                    "Download Successful.",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
        appUpdateManager.registerListener(updateListener!!)
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE,
            activity,
            UPDATE_REQUEST_CODE
        )
    }

    /**
     * Unregisters the update listener. Should be called in onPause().
     */
    fun unregisterListener() {
        updateListener?.let {
            appUpdateManager.unregisterListener(it)
            updateListener = null
        }
    }

    /**
     * Resumes the update flow. Should be called in onResume() to handle updates
     * that were initiated but not completed.
     */
    fun resumeUpdate(activity: Activity) {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    Toast.makeText(
                        context,
                        "Download Successful.",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
    }
}
