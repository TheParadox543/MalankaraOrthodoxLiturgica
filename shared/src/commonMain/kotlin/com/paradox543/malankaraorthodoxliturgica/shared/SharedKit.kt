package com.paradox543.malankaraorthodoxliturgica.shared

import com.paradox543.malankaraorthodoxliturgica.shared.di.initKoin
import com.paradox543.malankaraorthodoxliturgica.shared.prayer.PrayerApi
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform.getKoin

object SharedKit {
    private var initialized = false

    fun initialize(appDeclaration: KoinAppDeclaration = {}) {
        if (!initialized) {
            initKoin(appDeclaration)
            initialized = true
        }
    }

    fun getPrayerApi(): PrayerApi = getKoin().get()
}