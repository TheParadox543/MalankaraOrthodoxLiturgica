package com.paradox543.malankaraorthodoxliturgica.data.calendar.di

import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarComposeResourceReader
import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarSource
import com.paradox543.malankaraorthodoxliturgica.data.calendar.repository.CalendarRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val calendarDataModule =
    module {
        single<ResourceTextReader>(named("CalendarResourceReader")) { CalendarComposeResourceReader() }

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