package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.feature.song.viewmodel.SongPlayerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module

val songModule =
    module {
        single { getHiltBridge(androidContext()).songRepository() }
        single { getHiltBridge(androidContext()).translationsRepository() }

        viewModel {
            SongPlayerViewModel(
                context = androidContext(),
                songRepository = get(),
                settingsRepository = get(),
                translationsRepository = get(),
            )
        }
    }