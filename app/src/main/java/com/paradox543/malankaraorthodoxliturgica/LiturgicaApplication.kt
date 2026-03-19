package com.paradox543.malankaraorthodoxliturgica

import android.app.Application
import com.paradox543.malankaraorthodoxliturgica.data.bible.di.bibleDataModule
import com.paradox543.malankaraorthodoxliturgica.data.calendar.di.calendarDataModule
import com.paradox543.malankaraorthodoxliturgica.data.core.di.dataCoreBridgeModule
import com.paradox543.malankaraorthodoxliturgica.data.prayer.di.prayerDataModule
import com.paradox543.malankaraorthodoxliturgica.data.settings.di.settingsDataModule
import com.paradox543.malankaraorthodoxliturgica.data.song.di.songDataModule
import com.paradox543.malankaraorthodoxliturgica.data.translations.di.translationsDataModule
import com.paradox543.malankaraorthodoxliturgica.di.bibleModule
import com.paradox543.malankaraorthodoxliturgica.di.calendarModule
import com.paradox543.malankaraorthodoxliturgica.di.onboardingModule
import com.paradox543.malankaraorthodoxliturgica.di.platformKoinModule
import com.paradox543.malankaraorthodoxliturgica.di.prayerModule
import com.paradox543.malankaraorthodoxliturgica.di.settingsModule
import com.paradox543.malankaraorthodoxliturgica.di.songModule
import com.paradox543.malankaraorthodoxliturgica.di.startupModule
import com.paradox543.malankaraorthodoxliturgica.di.useCaseModule
import com.paradox543.malankaraorthodoxliturgica.platform.analytics.firebase.analyticsFirebaseModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

@HiltAndroidApp
class LiturgicaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LiturgicaApplication)
            workManagerFactory()

            modules(
                settingsDataModule,
                bibleDataModule,
                translationsDataModule,
                songDataModule,
                dataCoreBridgeModule,
                prayerDataModule,
                calendarDataModule,
                analyticsFirebaseModule,
                useCaseModule,
                startupModule,
                settingsModule,
                prayerModule,
                calendarModule,
                bibleModule,
                songModule,
                onboardingModule,
                platformKoinModule,
            )
        }
    }
}