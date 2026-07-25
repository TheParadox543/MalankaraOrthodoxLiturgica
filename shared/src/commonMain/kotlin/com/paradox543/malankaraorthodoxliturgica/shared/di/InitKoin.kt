package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.data.bible.di.bibleDataModule
import com.paradox543.malankaraorthodoxliturgica.data.calendar.di.calendarDataModule
import com.paradox543.malankaraorthodoxliturgica.data.core.di.dataCoreBridgeModule
import com.paradox543.malankaraorthodoxliturgica.data.prayer.di.prayerDataModule
import com.paradox543.malankaraorthodoxliturgica.data.translations.di.translationsDataModule
import com.paradox543.malankaraorthodoxliturgica.di.useCaseModule
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.di.prayerModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            bibleDataModule,
            calendarDataModule,
            dataCoreBridgeModule,
            prayerDataModule,
            translationsDataModule,
            useCaseModule,
            prayerModule,
            sharedModule,
            *platformModules().toTypedArray()
        )
    }
}

expect fun platformModules(): List<Module>
