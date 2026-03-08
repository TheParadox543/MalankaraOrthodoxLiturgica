package com.paradox543.malankaraorthodoxliturgica.services

import android.app.Activity
import android.content.Intent
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import javax.inject.Inject

class ShareServiceImpl @Inject constructor(
    private val analyticsService: AnalyticsService,
) : ShareService {
    /**
     * Launches an Android share intent to share the app's Play Store link.
     * @param shareMessage An optional custom message to include.
     * @param appPackageName Your app's package name.
     */
    override fun shareAppLink(
        activity: Activity,
        shareMessage: String,
        appPackageName: String,
    ) {
        val playStoreLink = "https://play.google.com/store/apps/details?id=$appPackageName"

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Check out this amazing app!")
                putExtra(Intent.EXTRA_TEXT, "$shareMessage\n$playStoreLink")
            }

        if (shareIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(
                Intent.createChooser(shareIntent, "Share App Via"),
            )

            analyticsService.logShareEvent()
        }
    }
}
