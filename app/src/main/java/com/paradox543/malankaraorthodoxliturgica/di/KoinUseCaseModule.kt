package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetAdjacentSiblingRoutesUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetDynamicSongsUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerNodesForCurrentTimeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerScreenContentUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetRecommendedPrayersUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetSongKeyPriorityUseCase
import org.koin.dsl.module

val useCaseModule =
    module {
        // Prayer domain use cases
        single { GetRecommendedPrayersUseCase() }

        single {
            GetPrayerNodesForCurrentTimeUseCase(
                getRecommendedPrayersUseCase = get(),
            )
        }

        single { GetAdjacentSiblingRoutesUseCase() }

        single {
            GetSongKeyPriorityUseCase(
                calendarRepository = get(),
            )
        }

        single {
            GetDynamicSongsUseCase(
                prayerRepository = get(),
                calendarRepository = get(),
            )
        }

        single {
            GetPrayerScreenContentUseCase(
                prayerRepository = get(),
                getDynamicSongsUseCase = get(),
            )
        }
    }