package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformAnalyticsModule(): Module = module {
    single<FirebaseAnalytics> {
        FirebaseAnalytics.getInstance(androidContext())
    }
    single<FirebasePlatformLogger> {
        AndroidFirebasePlatformLogger(firebaseAnalytics = get())
    }
}
