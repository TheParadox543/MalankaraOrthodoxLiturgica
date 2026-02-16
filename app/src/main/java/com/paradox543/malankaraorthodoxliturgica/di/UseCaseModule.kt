package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetDynamicSongsUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerNodesForCurrentTimeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerScreenContentUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetRecommendedPrayersUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

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

    @Provides
    fun provideGetDynamicSongsUseCase(
        prayerRepository: PrayerRepository,
        calendarRepository: CalendarRepository,
    ): GetDynamicSongsUseCase =
        GetDynamicSongsUseCase(
            prayerRepository = prayerRepository,
            calendarRepository = calendarRepository,
        )

    @Provides
    fun provideGetPrayerScreenContentUseCase(
        prayerRepository: PrayerRepository,
        getDynamicSongsUseCase: GetDynamicSongsUseCase,
    ): GetPrayerScreenContentUseCase =
        GetPrayerScreenContentUseCase(
            prayerRepository = prayerRepository,
            getDynamicSongsUseCase = getDynamicSongsUseCase,
        )
}