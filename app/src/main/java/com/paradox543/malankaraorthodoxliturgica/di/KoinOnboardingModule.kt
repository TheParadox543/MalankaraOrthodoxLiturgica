package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule =
    module {
        // Bridge Hilt dependencies into Koin
        single {
            getHiltBridge(androidContext()).settingsRepository()
        }

        single {
            getHiltBridge(androidContext()).analyticsService()
        }

        single {
            getHiltBridge(androidContext()).getPrayerScreenContentUseCase()
        }

        // ViewModel (pure Kotlin constructor)
        viewModel {
            OnboardingViewModel(
                settingsRepository = get(),
                analyticsService = get(),
                getPrayerScreenContentUseCase = get(),
            )
        }
    }