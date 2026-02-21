package com.paradox543.malankaraorthodoxliturgica.data.translations.di

import com.paradox543.malankaraorthodoxliturgica.data.translations.repository.TranslationsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TranslationsRepositoryModule {
    @Binds
    abstract fun bindTranslationsRepository(impl: TranslationsRepositoryImpl): TranslationsRepository
}