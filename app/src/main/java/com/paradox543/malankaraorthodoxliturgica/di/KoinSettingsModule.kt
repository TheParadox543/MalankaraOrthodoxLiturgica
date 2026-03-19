package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule =
    module {
        // Bridge Hilt dependencies into Koin
        single {
            getHiltBridge(androidContext()).analyticsService()
        }

        // ViewModel (pure Kotlin constructor)
        viewModel {
            SettingsViewModel(
                settingsRepository = get(),
                analyticsService = get(),
                soundModeManager = get(),
            )
        }
    }