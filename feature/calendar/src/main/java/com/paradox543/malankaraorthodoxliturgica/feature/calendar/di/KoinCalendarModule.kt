package com.paradox543.malankaraorthodoxliturgica.feature.calendar.di

import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val calendarModule =
    module {
        viewModel {
            CalendarViewModel(
                calendarRepository = get(),
                settingsRepository = get(),
                translationsRepository = get(),
                formatDateTitleUseCase = get(),
                loadBibleReadingUseCase = get(),
                formatGospelEntryUseCase = get(),
                formatBiblePrefaceUseCase = get(),
                formatBibleReadingEntryUseCase = get(),
            )
        }
    }