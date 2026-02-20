package com.paradox543.malankaraorthodoxliturgica.data.di

import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.PrayerRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.SettingsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.SongRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.TranslationsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.TranslationsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.song.repository.SongRepository
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
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository
}