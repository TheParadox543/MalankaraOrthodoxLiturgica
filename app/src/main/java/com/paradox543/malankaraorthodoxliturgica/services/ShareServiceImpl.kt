package com.paradox543.malankaraorthodoxliturgica.services

import android.content.Context
import android.content.Intent
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService

class ShareServiceImpl(
    private val context: Context,
    private val analyticsService: AnalyticsService,
) : ShareService {
    companion object {
        private const val APP_PACKAGE_NAME = "com.paradox543.malankaraorthodoxliturgica"
        private const val PLAY_STORE_BASE_URL = "https://play.google.com/store/apps/details"
    }

    /**
     * Launches an Android share intent to share the app's Play Store link.
     * @param shareMessage An optional custom message to include.
     */
    override fun shareAppLink(
        shareSubject: String,
        shareMessage: String,
    ) {
        val playStoreUrl = "$PLAY_STORE_BASE_URL?id=$APP_PACKAGE_NAME"
        val shareText = if (shareMessage.isNotEmpty()) "$shareMessage\n\n$playStoreUrl" else playStoreUrl

        val shareIntent =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, shareSubject)
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
        val chooserIntent =
            Intent.createChooser(shareIntent, "Share App Via").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(chooserIntent)
        analyticsService.logShareEvent()
    }
}
