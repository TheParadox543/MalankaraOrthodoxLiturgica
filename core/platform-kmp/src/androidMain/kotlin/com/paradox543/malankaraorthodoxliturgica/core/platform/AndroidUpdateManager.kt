package com.paradox543.malankaraorthodoxliturgica.core.platform

import android.app.Activity

/**
 * Abstraction over the Google Play In-App Update API.
 *
 * The concrete implementation lives in :app.
 */
interface AndroidUpdateManager : AppUpdateManager {
    /**
     * Required to launch Play Core update flow
     */
    fun bindActivity(activity: Activity)
}