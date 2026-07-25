package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent
import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.IOSInAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.IOSSoundModeCapability
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeCapability
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider
import com.paradox543.malankaraorthodoxliturgica.info.IOSAppInfoProvider
import org.koin.dsl.module

val iosPlatformModule =
    module {
        single<InAppReviewManager> { IOSInAppReviewManager() }
        single<AnalyticsService> {
            object : AnalyticsService {
                override fun logEvent(event: AnalyticsEvent) {
                    // No-op or print for iOS
                }
            }
        }
        single<SoundModeCapability> { IOSSoundModeCapability() }
        single<AppInfoProvider> { IOSAppInfoProvider() }
        single<ShareService> { IOSShareService(analyticsService = get()) }
    }
