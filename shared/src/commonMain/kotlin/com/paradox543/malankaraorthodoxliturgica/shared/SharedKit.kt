package com.paradox543.malankaraorthodoxliturgica.shared

import com.paradox543.malankaraorthodoxliturgica.shared.di.initKoin
import com.paradox543.malankaraorthodoxliturgica.shared.prayer.PrayerApi
import org.koin.mp.KoinPlatform.getKoin

object SharedKit {
    private var initialized = false

    fun initialize() {
        if (!initialized) {
            initKoin()
            initialized = true
        }
    }

    fun getPrayerApi(): PrayerApi = getKoin().get()
}