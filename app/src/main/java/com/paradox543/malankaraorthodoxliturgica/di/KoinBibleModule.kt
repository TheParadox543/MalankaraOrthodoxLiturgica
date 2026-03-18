package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.feature.bible.viewmodel.BibleViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bibleModule =
    module {
        viewModel {
            BibleViewModel(
                bibleRepository = get(),
                settingsRepository = get(),
                getAdjacentChaptersUseCase = get(),
            )
        }
    }