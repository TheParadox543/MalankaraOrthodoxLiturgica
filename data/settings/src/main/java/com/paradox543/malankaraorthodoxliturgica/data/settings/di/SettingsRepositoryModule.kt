package com.paradox543.malankaraorthodoxliturgica.data.settings.di

import com.paradox543.malankaraorthodoxliturgica.data.settings.repository.SettingsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsRepositoryModule {
    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}