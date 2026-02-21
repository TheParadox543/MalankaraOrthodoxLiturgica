package com.paradox543.malankaraorthodoxliturgica.data.prayer.di

import com.paradox543.malankaraorthodoxliturgica.data.prayer.repository.PrayerRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PrayerRepositoryModule {
    @Binds
    abstract fun bindPrayerRepository(impl: PrayerRepositoryImpl): PrayerRepository
}