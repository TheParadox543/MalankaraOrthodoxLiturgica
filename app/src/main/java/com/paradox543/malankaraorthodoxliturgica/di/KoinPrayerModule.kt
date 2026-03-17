package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerNavViewModel
import com.paradox543.malankaraorthodoxliturgica.feature.prayer.viewmodel.PrayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val prayerModule =
    module {
        // Prayer-specific bridge deps only (avoid redefining deps already in settings/song modules)
        single { getHiltBridge(androidContext()).inAppReviewManager() }
        single { getHiltBridge(androidContext()).prayerRepository() }
        single { getHiltBridge(androidContext()).getPrayerScreenContentUseCase() }
        single { getHiltBridge(androidContext()).getSongKeyPriorityUseCase() }
        single { getHiltBridge(androidContext()).getAdjacentSiblingRoutesUseCase() }
        single { getHiltBridge(androidContext()).getPrayerNodesForCurrentTimeUseCase() }

        viewModel {
            PrayerViewModel(
                settingsRepository = get(),
                translationsRepository = get(),
                analyticsService = get(),
                inAppReviewManager = get(),
                getPrayerScreenContentUseCase = get(),
                getSongKeyPriorityUseCase = get(),
            )
        }

        viewModel {
            PrayerNavViewModel(
                settingsRepository = get(),
                prayerRepository = get(),
                getAdjacentSiblingRoutesUseCase = get(),
                getPrayerNodesForCurrentTimeUseCase = get(),
            )
        }
    }