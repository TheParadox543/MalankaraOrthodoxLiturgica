package com.paradox543.malankaraorthodoxliturgica.data.calendar.di

import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarSource
import com.paradox543.malankaraorthodoxliturgica.data.calendar.repository.CalendarRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.SyncResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val calendarDataModule =
    module {
        single { CalendarBundledContentSource(get()) } bind DomainBundledContentSource::class

        single<ResourceTextReader>(named("CalendarResourceReader")) { get<SyncResourceTextReader>() }

        single {
            CalendarSource(
                reader = get(named("CalendarResourceReader")),
                json = get(),
            )
        }

        single<CalendarRepository> {
            CalendarRepositoryImpl(calendarSource = get())
        }
    }
