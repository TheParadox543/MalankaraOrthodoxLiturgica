package com.paradox543.malankaraorthodoxliturgica.di

import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.paradox543.malankaraorthodoxliturgica.services.sound.SoundModeManager as SoundModeManagerImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class PlatformModule {
    @Binds
    @Singleton
    abstract fun bindSoundModeManager(impl: SoundModeManagerImpl): SoundModeManager
}