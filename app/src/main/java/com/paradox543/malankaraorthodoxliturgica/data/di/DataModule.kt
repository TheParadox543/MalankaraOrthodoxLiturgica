package com.paradox543.malankaraorthodoxliturgica.data.di

import com.paradox543.malankaraorthodoxliturgica.data.repository.PrayerRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Provides bindings for implementation of repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindPrayerRepository(impl: PrayerRepositoryImpl): PrayerRepository
}