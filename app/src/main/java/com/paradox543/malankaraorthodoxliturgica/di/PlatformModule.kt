package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.services.FirebaseAnalyticsService
import com.paradox543.malankaraorthodoxliturgica.services.InAppReviewManagerImpl
import com.paradox543.malankaraorthodoxliturgica.services.InAppUpdateManagerImpl
import com.paradox543.malankaraorthodoxliturgica.services.ShareServiceImpl
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

    @Binds
    @Singleton
    abstract fun bindinAppReviewManager(impl: InAppReviewManagerImpl): InAppReviewManager

    @Binds
    @Singleton
    abstract fun bindInAppUpdateManager(impl: InAppUpdateManagerImpl): InAppUpdateManager

    @Binds
    @Singleton
    abstract fun bindShareServiceManager(impl: ShareServiceImpl): ShareService
}