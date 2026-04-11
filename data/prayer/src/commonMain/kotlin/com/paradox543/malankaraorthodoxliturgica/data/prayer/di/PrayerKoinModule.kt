package com.paradox543.malankaraorthodoxliturgica.data.prayer.di

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource.PrayerComposeResourceReader
import com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.prayer.repository.PrayerRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val prayerDataModule =
    module {
        single<ResourceTextReader>(named("PrayerResourceReader")) { PrayerComposeResourceReader() }

        single {
            PrayerSource(
                reader = get(named("PrayerResourceReader")),
                json = get(),
            )
        }

        single<PrayerRepository> {
            PrayerRepositoryImpl(prayerSource = get())
        }
    }