package com.paradox543.malankaraorthodoxliturgica.logging.di

import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import org.koin.dsl.module

val loggingModule = module {
    single(createdAtStart = true) {
        val appInfo: AppInfoProvider = get()
        AppLogger.initialize(appInfo.debugMode)
        AppLogger
    }
}
