package com.paradox543.malankaraorthodoxliturgica.feature.prayer.di

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerScreenContentUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetSongKeyPriorityUseCase
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val prayerModule =
    module {
        viewModel {
            PrayerViewModel(
                settingsRepository = get(),
                translationsRepository = get(),
                analyticsService = get(),
                loadPrayerScreenContent = { filename, language ->
                    get<GetPrayerScreenContentUseCase>().invoke(filename, language)
                },
                getSongKeyPriority = {
                    get<GetSongKeyPriorityUseCase>().invoke()
                },
            )
        }

        viewModel {
            PrayerNavViewModel(
                settingsRepository = get(),
                prayerRepository = get(),
                getAdjacentSiblingRoutesUseCase = get(),
                getPrayerNodesForCurrentTimeUseCase = get(),
                createPrayerIndexUseCase = get(),
                inAppReviewManager = get(),
                contentUpdateSignal = get(),
            )
        }
    }