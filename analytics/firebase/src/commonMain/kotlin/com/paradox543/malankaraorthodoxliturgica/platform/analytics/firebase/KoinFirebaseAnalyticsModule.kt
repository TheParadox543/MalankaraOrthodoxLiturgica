package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import com.paradox543.malankaraorthodoxliturgica.core.analytics.AnalyticsService
import org.koin.core.module.Module
import org.koin.dsl.module

val analyticsFirebaseModule: Module = module {
    single<AnalyticsService> {
        FirebaseAnalyticsService(
            logger = get(),
            appInfoProvider = get()
        )
    }
}

expect fun platformAnalyticsModule(): Module
