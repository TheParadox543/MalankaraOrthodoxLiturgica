package com.paradox543.malankaraorthodoxliturgica.di

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBiblePrefaceUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleRangeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleReadingEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatGospelEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.GetAdjacentChaptersUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.LoadBibleReadingUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.usecase.FormatDateTitleUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltBridge {
    fun analyticsService(): AnalyticsService

    fun soundModeManager(): SoundModeManager

    fun inAppReviewManager(): InAppReviewManager

    fun bibleRepository(): BibleRepository

    fun getAdjacentChaptersUseCase(): GetAdjacentChaptersUseCase

    fun calendarRepository(): CalendarRepository

    fun formatDateTitleUseCase(): FormatDateTitleUseCase

    fun loadBibleReadingUseCase(): LoadBibleReadingUseCase

    fun formatBibleRangeUseCase(): FormatBibleRangeUseCase

    fun formatBibleReadingEntryUseCase(): FormatBibleReadingEntryUseCase

    fun formatGospelEntryUseCase(): FormatGospelEntryUseCase

    fun formatBiblePrefaceUseCase(): FormatBiblePrefaceUseCase
}

fun getHiltBridge(context: Context): HiltBridge {
    val appContext = context.applicationContext
    return EntryPointAccessors.fromApplication(
        appContext,
        HiltBridge::class.java,
    )
}