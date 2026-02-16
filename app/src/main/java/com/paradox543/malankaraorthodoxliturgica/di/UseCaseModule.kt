package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerNodesForCurrentTimeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetRecommendedPrayersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetRecommendedPrayersUseCase(): GetRecommendedPrayersUseCase = GetRecommendedPrayersUseCase()

    @Provides
    fun provideGetPrayerNodesForCurrentTimeUseCase(): GetPrayerNodesForCurrentTimeUseCase =
        GetPrayerNodesForCurrentTimeUseCase(
            GetRecommendedPrayersUseCase(),
        )
}