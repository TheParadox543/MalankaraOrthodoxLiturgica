package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.ui.viewmodel.StartupViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val startupModule =
    module {
        viewModel {
            StartupViewModel(
                settingsRepository = get(),
            )
        }
    }