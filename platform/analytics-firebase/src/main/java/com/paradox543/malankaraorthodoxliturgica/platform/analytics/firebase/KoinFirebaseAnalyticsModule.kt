package com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsFirebaseModule =
    module {
        single<FirebaseAnalytics> {
            FirebaseAnalytics.getInstance(androidContext())
        }

        single<AnalyticsService> {
            FirebaseAnalyticsService(
                firebaseAnalytics = get(),
            )
        }
    }