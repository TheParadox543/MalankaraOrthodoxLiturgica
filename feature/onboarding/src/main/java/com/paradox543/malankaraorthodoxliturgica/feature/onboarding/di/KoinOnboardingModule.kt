package com.paradox543.malankaraorthodoxliturgica.feature.onboarding.di

import com.paradox543.malankaraorthodoxliturgica.feature.onboarding.viewmodel.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule =
    module {
        viewModel {
            OnboardingViewModel(
                settingsRepository = get(),
                analyticsService = get(),
                getPrayerScreenContentUseCase = get(),
            )
        }
    }