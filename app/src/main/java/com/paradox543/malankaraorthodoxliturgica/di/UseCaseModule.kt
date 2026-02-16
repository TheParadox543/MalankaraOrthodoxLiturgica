package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBiblePrefaceUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleRangeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleReadingEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatGospelEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.LoadBibleReadingUseCase
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
    // Prayer Use Cases
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

    // Bible Use Cases
    @Provides
    fun provideBiblePrefaceUseCase(bibleRepository: BibleRepository): FormatBiblePrefaceUseCase =
        FormatBiblePrefaceUseCase(
            bibleRepository = bibleRepository,
        )

    @Provides
    fun provideBibleRangeUseCase(): FormatBibleRangeUseCase = FormatBibleRangeUseCase()

    @Provides
    fun provideBibleReadingEntryUseCase(bibleRepository: BibleRepository): FormatBibleReadingEntryUseCase =
        FormatBibleReadingEntryUseCase(
            bibleRepository = bibleRepository,
            formatBibleRangeUseCase = FormatBibleRangeUseCase(),
        )

    @Provides
    fun provideGospelEntryUseCase(
        bibleRepository: BibleRepository,
        formatBibleRangeUseCase: FormatBibleRangeUseCase,
    ): FormatGospelEntryUseCase =
        FormatGospelEntryUseCase(
            FormatBibleReadingEntryUseCase(
                bibleRepository = bibleRepository,
                formatBibleRangeUseCase = formatBibleRangeUseCase,
            ),
        )

    @Provides
    fun provideBibleReadingUseCase(bibleRepository: BibleRepository): LoadBibleReadingUseCase =
        LoadBibleReadingUseCase(bibleRepository = bibleRepository)
}