package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule =
    module {
        viewModel {
            SettingsViewModel(
                settingsRepository = get(),
                analyticsService = get(),
                soundModeManager = get(),
            )
        }
    }