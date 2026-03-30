package com.paradox543.malankaraorthodoxliturgica.data.translations.di

import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.RawTranslationsSource
import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.TranslationSource
import com.paradox543.malankaraorthodoxliturgica.data.translations.repository.TranslationsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import org.koin.dsl.module

val translationsDataModule =
    module {
        single<RawTranslationsSource> { TranslationSource(reader = get()) }

        single<TranslationsRepository> {
            TranslationsRepositoryImpl(source = get())
        }
    }