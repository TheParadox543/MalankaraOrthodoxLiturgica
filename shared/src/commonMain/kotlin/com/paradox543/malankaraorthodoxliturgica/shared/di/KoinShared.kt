package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.shared.prayer.PrayerApi
import org.koin.dsl.module

val sharedModule =
    module {
        single { PrayerApi(get()) }
    }