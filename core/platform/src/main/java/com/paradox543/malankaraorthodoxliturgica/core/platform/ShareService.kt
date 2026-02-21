package com.paradox543.malankaraorthodoxliturgica.core.platform

import android.app.Activity

/**
 * Abstraction over the system share sheet and associated analytics.
 *
 * The concrete implementation lives in :app.
 */
interface ShareService {
    /**
     * Launches an Android share intent to share the app's Play Store link.
     *
     * @param activity    The currently visible [Activity] used to start the chooser intent.
     * @param shareMessage An optional custom message prepended to the Play Store link.
     * @param appPackageName The app's package name used to build the Play Store URL.
     */
    fun shareAppLink(
        activity: Activity,
        shareMessage: String = "",
        appPackageName: String = "com.paradox543.malankaraorthodoxliturgica",
    )
}
