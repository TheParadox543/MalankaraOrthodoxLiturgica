package com.paradox543.malankaraorthodoxliturgica.data.prayer.di

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource.PrayerBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.prayer.repository.PrayerRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.SyncResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.dsl.bind

val prayerDataModule =
    module {
        single { PrayerBundledContentSource(get()) } bind DomainBundledContentSource::class

        single<ResourceTextReader>(named("PrayerResourceReader")) { get<SyncResourceTextReader>() }

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
