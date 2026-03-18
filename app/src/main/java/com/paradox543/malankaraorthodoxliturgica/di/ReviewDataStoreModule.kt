package com.paradox543.malankaraorthodoxliturgica.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.reviewDataStore by preferencesDataStore(name = "review_prefs")

@Module
@InstallIn(SingletonComponent::class)
object ReviewDataStoreModule {
    @Provides
    @Singleton
    @ReviewDataStore
    fun provideReviewDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.reviewDataStore
}