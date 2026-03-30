package com.paradox543.malankaraorthodoxliturgica.data.prayer.di

import com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.prayer.repository.PrayerRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import org.koin.dsl.module

val prayerDataModule =
    module {
        single { PrayerSource(reader = get()) }

        single<PrayerRepository> {
            PrayerRepositoryImpl(prayerSource = get())
        }
    }