package com.paradox543.malankaraorthodoxliturgica.data.di

import com.paradox543.malankaraorthodoxliturgica.data.repository.CalendarRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repository.PrayerRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repository.TranslationsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.repository.TranslationsRepository
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
    abstract fun bindTranslationsRepository(impl: TranslationsRepositoryImpl): TranslationsRepository

    @Binds
    abstract fun bindPrayerRepository(impl: PrayerRepositoryImpl): PrayerRepository

    @Binds
    abstract fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository
}