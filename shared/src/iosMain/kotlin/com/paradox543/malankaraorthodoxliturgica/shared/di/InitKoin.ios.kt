package com.paradox543.malankaraorthodoxliturgica.shared.di

import com.paradox543.malankaraorthodoxliturgica.data.settings.di.iosSettingsDataModule
import org.koin.core.module.Module

actual fun platformModules(): List<Module> = listOf(
    iosSettingsDataModule,
    iosPlatformModule
)
