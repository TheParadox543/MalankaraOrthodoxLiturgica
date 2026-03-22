package com.paradox543.malankaraorthodoxliturgica.data.calendar.di

import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarSource
import com.paradox543.malankaraorthodoxliturgica.data.calendar.repository.CalendarRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import org.koin.dsl.module

val calendarDataModule =
    module {
        single { CalendarSource(reader = get()) }

        single<CalendarRepository> {
            CalendarRepositoryImpl(calendarSource = get())
        }
    }