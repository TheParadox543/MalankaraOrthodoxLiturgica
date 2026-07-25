package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.IOSInAppReviewManager
import org.koin.dsl.module

val iosPlatformModule = module {
    single<InAppReviewManager> { IOSInAppReviewManager() }
    single<AnalyticsService> { 
        object : AnalyticsService {
            override fun logEvent(event: com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsEvent) {
                // No-op or print for iOS
            }
        }
    }
}
