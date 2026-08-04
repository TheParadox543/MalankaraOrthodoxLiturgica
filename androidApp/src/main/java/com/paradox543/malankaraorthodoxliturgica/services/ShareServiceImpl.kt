package com.paradox543.malankaraorthodoxliturgica.services

import android.content.Context
import android.content.Intent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService

class ShareServiceImpl(
    private val context: Context,
    private val analyticsService: AnalyticsService,
) : ShareService {
    companion object {
        private const val APP_SHARE_URL = "https://theparadox543.github.io/MalankaraOrthodoxLiturgica/download"
    }

    /**
     * Launches an Android share intent to share the app's landing page.
     * @param shareMessage An optional custom message to include.
     */
    override fun shareAppLink(
        shareSubject: String,
        shareMessage: String,
    ) {
        val shareText = if (shareMessage.isNotEmpty()) "$shareMessage\n\n$APP_SHARE_URL" else APP_SHARE_URL

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
        analyticsService.logEvent(AnalyticsEvent.ShareApp)
    }
}
