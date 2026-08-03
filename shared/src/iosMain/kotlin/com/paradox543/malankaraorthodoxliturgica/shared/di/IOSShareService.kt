package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

class IOSShareService(
    private val analyticsService: AnalyticsService,
) : ShareService {
    override fun shareAppLink(
        shareSubject: String,
        shareMessage: String,
    ) {
        val shareText = shareMessage.ifEmpty { shareSubject }
        val activityViewController = UIActivityViewController(
            activityItems = listOf(shareText),
            applicationActivities = null,
        )
        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?.presentViewController(activityViewController, animated = true, completion = null)
        analyticsService.logEvent(AnalyticsEvent.ShareApp)
    }
}
