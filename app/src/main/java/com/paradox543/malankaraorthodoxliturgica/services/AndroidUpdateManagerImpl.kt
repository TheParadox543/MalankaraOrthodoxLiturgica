package com.paradox543.malankaraorthodoxliturgica.services

import android.app.Activity
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo as GooglePlayAppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager as GooglePlayAppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.paradox543.malankaraorthodoxliturgica.core.platform.AndroidUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.model.AppUpdateInfo
import com.paradox543.malankaraorthodoxliturgica.core.platform.model.UpdateType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidUpdateManagerImpl(
    private val appUpdateManager: GooglePlayAppUpdateManager,
) : AndroidUpdateManager {
    companion object {
        const val UPDATE_REQUEST_CODE = 123
        private const val TAG = "AndroidUpdateManagerImpl"
    }

    private val _updateReady = MutableStateFlow(false)
    override val updateReady: StateFlow<Boolean> = _updateReady.asStateFlow()

    private var boundActivity: Activity? = null
    private var pendingInfo: GooglePlayAppUpdateInfo? = null
    private var updateListener: InstallStateUpdatedListener? = null

    override fun bindActivity(activity: Activity) {
        boundActivity = activity
    }

    override suspend fun checkForUpdate(): AppUpdateInfo? =
        suspendCancellableCoroutine { continuation ->
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                pendingInfo = appUpdateInfo

                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    _updateReady.value = true
                }

                val availability = appUpdateInfo.updateAvailability()
                val model =
                    when (availability) {
                        UpdateAvailability.UPDATE_AVAILABLE,
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS,
                        -> {
                            val immediateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                            val flexibleAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                            if (immediateAllowed || flexibleAllowed) {
                                AppUpdateInfo(
                                    isUpdateAvailable = true,
                                    isForceUpdate = appUpdateInfo.updatePriority() >= 3 && immediateAllowed,
                                    availableVersion = "",
                                )
                            } else {
                                null
                            }
                        }

                        else -> null
                    }

                if (continuation.isActive) {
                    continuation.resume(model)
                }
            }

            appUpdateInfoTask.addOnFailureListener { error ->
                Log.e(TAG, "Failed to check for updates.", error)
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }

    override fun startUpdate(updateType: UpdateType) {
        val activity = boundActivity
        val appUpdateInfo = pendingInfo
        if (activity == null || appUpdateInfo == null) {
            Log.d(TAG, "Cannot start update; activity or update info missing")
            return
        }

        val playCoreType =
            when (updateType) {
                UpdateType.FLEXIBLE -> AppUpdateType.FLEXIBLE
                UpdateType.IMMEDIATE -> AppUpdateType.IMMEDIATE
            }

        if (playCoreType == AppUpdateType.FLEXIBLE) {
            registerFlexibleListenerIfNeeded()
        }

        val options = AppUpdateOptions.newBuilder(playCoreType).build()
        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, activity, options, UPDATE_REQUEST_CODE)
    }

    override fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    override fun onResume() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                pendingInfo = appUpdateInfo
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    _updateReady.value = true
                }
            }
    }

    override fun onPause() {
        updateListener?.let {
            appUpdateManager.unregisterListener(it)
            updateListener = null
        }
    }

    private fun registerFlexibleListenerIfNeeded() {
        if (updateListener != null) return

        updateListener =
            InstallStateUpdatedListener { state ->
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    _updateReady.value = true
                    onPause()
                }
            }

        appUpdateManager.registerListener(updateListener!!)
    }
}