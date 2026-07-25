package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.data.core.di.dataCoreBridgeModule
import com.paradox543.malankaraorthodoxliturgica.data.prayer.di.prayerDataModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            dataCoreBridgeModule,
            prayerDataModule,
            sharedModule,
            // add other modules later
        )
    }
}