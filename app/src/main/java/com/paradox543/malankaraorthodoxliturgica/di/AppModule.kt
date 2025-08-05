package com.paradox543.malankaraorthodoxliturgica.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.paradox543.malankaraorthodoxliturgica.data.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.LiturgicalCalendarRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.NavigationRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepository
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
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository = SettingsRepository(context)

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
    ): PrayerRepository = PrayerRepository(context, Json)

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
    ): LiturgicalCalendarRepository = LiturgicalCalendarRepository(context, json)

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("app_settings") }
        )
    }

    // Hilt will automatically provide the Context and DataStore it needs.
    @Singleton
    @Provides
    fun provideReviewManager(@ApplicationContext context: Context): ReviewManager {
        return ReviewManagerFactory.create(context)
    }

    @Singleton
    @Provides
    fun provideAppUpdateManager(@ApplicationContext context: Context): AppUpdateManager {
        return AppUpdateManagerFactory.create(context)
    }
}