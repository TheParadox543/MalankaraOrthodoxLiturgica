package com.paradox543.malankaraorthodoxliturgica.data.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.paradox543.malankaraorthodoxliturgica.data.settings.repository.SettingsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsRepositoryModule {
    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideSettingsDataStore(
            @ApplicationContext context: Context,
        ): DataStore<Preferences> = context.settingsDataStore
    }
}