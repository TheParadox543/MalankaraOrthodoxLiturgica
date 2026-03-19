package com.paradox543.malankaraorthodoxliturgica.di

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.core.platform.AnalyticsService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltBridge {
    fun analyticsService(): AnalyticsService
}

fun getHiltBridge(context: Context): HiltBridge {
    val appContext = context.applicationContext
    return EntryPointAccessors.fromApplication(
        appContext,
        HiltBridge::class.java,
    )
}