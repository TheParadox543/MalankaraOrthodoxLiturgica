package com.paradox543.malankaraorthodoxliturgica.di

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.services.InAppUpdateManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val platformKoinModule =
    module {
        single<AppUpdateManager> { AppUpdateManagerFactory.create(androidContext()) }

        single<InAppUpdateManager> {
            InAppUpdateManagerImpl(
                appUpdateManager = get(),
            )
        }
    }