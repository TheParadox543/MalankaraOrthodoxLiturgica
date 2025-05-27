package com.paradox543.malankaraorthodoxliturgica.di

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.DataStoreManager
import com.paradox543.malankaraorthodoxliturgica.data.repository.NavigationRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.PrayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePrayerRepository(
        @ApplicationContext context: Context
    ): PrayerRepository = PrayerRepository(context)

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context
    ): DataStoreManager = DataStoreManager(context)

    @Provides
    @Singleton
    fun provideNavigationRepository(
        @ApplicationContext context: Context
    ): NavigationRepository = NavigationRepository(context)

    @Provides
    @Singleton
    fun provideBibleRepository(
        @ApplicationContext context: Context
    ): BibleRepository = BibleRepository(context)

    @Provides
    @Singleton
    fun provideCalendarRepository(
        @ApplicationContext context: Context
    ): CalendarRepository = CalendarRepository(context)
}