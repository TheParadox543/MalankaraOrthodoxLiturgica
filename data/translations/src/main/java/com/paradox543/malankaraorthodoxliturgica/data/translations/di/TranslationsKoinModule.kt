package com.paradox543.malankaraorthodoxliturgica.data.translations.di

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.TranslationSource
import com.paradox543.malankaraorthodoxliturgica.data.translations.repository.TranslationsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val translationsDataModule =
    module {
        single {
            Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }

        single {
            AssetJsonReader(
                context = get<Context>(),
                json = get(),
            )
        }

        single { TranslationSource(reader = get()) }

        single<TranslationsRepository> {
            TranslationsRepositoryImpl(source = get())
        }
    }