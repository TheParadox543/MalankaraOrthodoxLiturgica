package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBiblePrefaceUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleRangeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleReadingEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatGospelEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.GetAdjacentChaptersUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.LoadBibleReadingUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.usecase.FormatDateTitleUseCase
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

        single {
            GetAdjacentChaptersUseCase(
                bibleRepository = get(),
            )
        }

        single {
            LoadBibleReadingUseCase(
                bibleRepository = get(),
            )
        }

        single {
            FormatBiblePrefaceUseCase(
                bibleRepository = get(),
            )
        }

        single {
            FormatBibleRangeUseCase()
        }

        single {
            FormatBibleReadingEntryUseCase(
                bibleRepository = get(),
                formatBibleRangeUseCase = get(),
            )
        }

        single {
            FormatGospelEntryUseCase(
                formatBibleReadingEntryUseCase = get(),
            )
        }

        single {
            FormatDateTitleUseCase()
        }
    }