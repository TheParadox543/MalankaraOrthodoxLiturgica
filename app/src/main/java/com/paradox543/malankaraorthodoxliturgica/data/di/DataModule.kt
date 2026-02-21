package com.paradox543.malankaraorthodoxliturgica.data.di

import com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl.SongRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.song.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Provides bindings for implementation of repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository
}