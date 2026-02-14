package com.paradox543.malankaraorthodoxliturgica.data.di

import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.BibleRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.CalendarRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.PrayerRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.SettingsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.SongRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.TranslationsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.repository.SongRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.TranslationsRepository
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

    @Binds
    abstract fun bindBibleRepository(impl: BibleRepositoryImpl): BibleRepository

    @Binds
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository
}