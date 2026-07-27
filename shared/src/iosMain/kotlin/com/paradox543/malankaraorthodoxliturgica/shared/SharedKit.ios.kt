package com.paradox543.malankaraorthodoxliturgica.shared

import com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase.NativeAnalyticsLogger
import org.koin.dsl.module

fun SharedKit.initialize(nativeAnalyticsLogger: NativeAnalyticsLogger) {
    this.initialize {
        modules(
            module {
                single<NativeAnalyticsLogger> { nativeAnalyticsLogger }
            }
        )
    }
}
