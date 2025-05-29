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
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext context: Context
    ): DataStoreManager = DataStoreManager(context)

    @Provides
    @Singleton // Ensure only one instance of Json is created
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true // Important for robust parsing
            prettyPrint = true      // For readability if you ever debug JSON output
            isLenient = true        // Allows for some non-strict JSON (e.g., unquoted keys if needed)
        }
    }
    @Provides
    @Singleton
    fun providePrayerRepository(
        @ApplicationContext context: Context
    ): PrayerRepository = PrayerRepository(context)

    @Provides
    @Singleton
    fun provideNavigationRepository(
        @ApplicationContext context: Context
    ): NavigationRepository = NavigationRepository(context)

    @Provides
    @Singleton
    fun provideBibleRepository(
        @ApplicationContext context: Context,
        json: Json
    ): BibleRepository = BibleRepository(context, json)

    @Provides
    @Singleton
    fun provideCalendarRepository(
        @ApplicationContext context: Context,
        json: Json
    ): CalendarRepository = CalendarRepository(context, json)
}