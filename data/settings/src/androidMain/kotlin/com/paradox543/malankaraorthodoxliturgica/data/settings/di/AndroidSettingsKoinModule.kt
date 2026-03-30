package com.paradox543.malankaraorthodoxliturgica.data.settings.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.paradox543.malankaraorthodoxliturgica.data.settings.repository.AndroidSettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import org.koin.dsl.module

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

val androidSettingsDataModule =
    module {
        single<DataStore<Preferences>> { get<Context>().settingsDataStore }

        single<SettingsRepository> {
            AndroidSettingsRepository(dataStore = get())
        }
    }