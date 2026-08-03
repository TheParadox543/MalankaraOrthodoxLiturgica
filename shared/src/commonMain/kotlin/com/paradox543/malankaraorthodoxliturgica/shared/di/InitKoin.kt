package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.data.bible.di.bibleDataModule
import com.paradox543.malankaraorthodoxliturgica.data.calendar.di.calendarDataModule
import com.paradox543.malankaraorthodoxliturgica.data.core.di.dataCoreBridgeModule
import com.paradox543.malankaraorthodoxliturgica.data.prayer.di.prayerDataModule
import com.paradox543.malankaraorthodoxliturgica.data.sync.di.platformSyncModule
import com.paradox543.malankaraorthodoxliturgica.data.sync.di.syncKoinModule
import com.paradox543.malankaraorthodoxliturgica.data.translations.di.translationsDataModule
import com.paradox543.malankaraorthodoxliturgica.di.useCaseModule
import com.paradox543.malankaraorthodoxliturgica.feature.bible.di.bibleModule
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.di.calendarModule
import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.di.onboardingModule
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.di.prayerModule
import com.paradox543.malankaraorthodoxliturgica.feature.settings.di.settingsModule
import com.paradox543.malankaraorthodoxliturgica.logging.di.loggingModule
import com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase.analyticsFirebaseModule
import com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase.platformAnalyticsModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            loggingModule,
            syncKoinModule,
            platformSyncModule,
            bibleDataModule,
            calendarDataModule,
            dataCoreBridgeModule,
            prayerDataModule,
            translationsDataModule,
            useCaseModule,
            prayerModule,
            calendarModule,
            bibleModule,
            settingsModule,
            onboardingModule,
            analyticsFirebaseModule,
            platformAnalyticsModule(),
            sharedModule,
            *platformModules().toTypedArray()
        )
    }
}


expect fun platformModules(): List<Module>
