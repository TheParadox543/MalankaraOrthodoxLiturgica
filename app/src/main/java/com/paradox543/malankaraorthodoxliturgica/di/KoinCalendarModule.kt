package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val calendarModule =
    module {
        single { getHiltBridge(androidContext()).calendarRepository() }
        single { getHiltBridge(androidContext()).formatDateTitleUseCase() }

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