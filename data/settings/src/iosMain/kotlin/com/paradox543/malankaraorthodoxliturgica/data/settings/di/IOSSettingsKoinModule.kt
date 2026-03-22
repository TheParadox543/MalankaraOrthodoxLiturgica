package com.paradox543.malankaraorthodoxliturgica.data.settings.di

import com.paradox543.malankaraorthodoxliturgica.data.settings.repository.IOSSettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import org.koin.dsl.module

val iosSettingsDataModule =
    module {
        single<SettingsRepository> {
            IOSSettingsRepository()
        }
    }