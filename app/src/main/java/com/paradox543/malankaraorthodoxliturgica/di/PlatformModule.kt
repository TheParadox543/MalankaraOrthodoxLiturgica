package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.services.FirebaseAnalyticsService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlatformModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsService(impl: FirebaseAnalyticsService): AnalyticsService
}