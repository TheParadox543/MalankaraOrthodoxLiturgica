package com.paradox543.malankaraorthodoxliturgica.di

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.repository.PrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetAdjacentSiblingRoutesUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerNodesForCurrentTimeUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetPrayerScreenContentUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetSongKeyPriorityUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.song.repository.SongRepository
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltBridge {
    fun settingsRepository(): SettingsRepository

    fun translationsRepository(): TranslationsRepository

    fun songRepository(): SongRepository

    fun analyticsService(): AnalyticsService

    fun soundModeManager(): SoundModeManager

    fun inAppReviewManager(): InAppReviewManager

    fun prayerRepository(): PrayerRepository

    fun getPrayerScreenContentUseCase(): GetPrayerScreenContentUseCase

    fun getSongKeyPriorityUseCase(): GetSongKeyPriorityUseCase

    fun getAdjacentSiblingRoutesUseCase(): GetAdjacentSiblingRoutesUseCase

    fun getPrayerNodesForCurrentTimeUseCase(): GetPrayerNodesForCurrentTimeUseCase
}

fun getHiltBridge(context: Context): HiltBridge {
    val appContext = context.applicationContext
    return EntryPointAccessors.fromApplication(
        appContext,
        HiltBridge::class.java,
    )
}