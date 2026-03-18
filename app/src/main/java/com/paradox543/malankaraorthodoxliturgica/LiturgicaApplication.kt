package com.paradox543.malankaraorthodoxliturgica

import android.app.Application
import com.paradox543.malankaraorthodoxliturgica.data.translations.di.translationsDataModule
import com.paradox543.malankaraorthodoxliturgica.di.bibleModule
import com.paradox543.malankaraorthodoxliturgica.di.calendarModule
import com.paradox543.malankaraorthodoxliturgica.di.onboardingModule
import com.paradox543.malankaraorthodoxliturgica.di.prayerModule
import com.paradox543.malankaraorthodoxliturgica.di.settingsModule
import com.paradox543.malankaraorthodoxliturgica.di.songModule
import com.paradox543.malankaraorthodoxliturgica.di.startupModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@HiltAndroidApp
class LiturgicaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@LiturgicaApplication)

            modules(
                translationsDataModule,
                startupModule,
                settingsModule,
                prayerModule,
                calendarModule,
                bibleModule,
                songModule,
                onboardingModule,
            )
        }
    }
}