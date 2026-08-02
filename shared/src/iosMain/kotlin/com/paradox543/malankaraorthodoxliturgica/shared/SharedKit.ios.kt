package com.paradox543.malankaraorthodoxliturgica.shared

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.NativeRemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.Synchronizer
import com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase.NativeAnalyticsLogger
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

fun SharedKit.initialize(
    nativeAnalyticsLogger: NativeAnalyticsLogger,
    nativeRemoteContentSource: NativeRemoteContentSource
) {
    this.initialize {
        modules(
            module {
                single<NativeAnalyticsLogger> { nativeAnalyticsLogger }
                single<NativeRemoteContentSource> { nativeRemoteContentSource }
            }
        )
    }
}

fun SharedKit.getSynchronizer(): Synchronizer = getKoin().get()
