package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformAnalyticsModule(): Module = module {
    single<FirebasePlatformLogger> {
        IOSFirebasePlatformLogger(nativeLogger = get())
    }
}
