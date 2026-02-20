package com.paradox543.malankaraorthodoxliturgica.data.bible.di

import com.paradox543.malankaraorthodoxliturgica.data.bible.repository.BibleRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BibleRepositoryModule {
    @Binds
    abstract fun bindBibleRepository(impl: BibleRepositoryImpl): BibleRepository
}