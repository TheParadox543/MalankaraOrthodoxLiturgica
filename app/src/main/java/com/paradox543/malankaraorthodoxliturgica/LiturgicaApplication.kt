package com.paradox543.malankaraorthodoxliturgica

import android.app.Application
import com.paradox543.malankaraorthodoxliturgica.di.settingsModule
import com.paradox543.malankaraorthodoxliturgica.di.songModule
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
                settingsModule,
                songModule,
            )
        }
    }
}