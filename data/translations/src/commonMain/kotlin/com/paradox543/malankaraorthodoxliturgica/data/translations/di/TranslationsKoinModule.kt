package com.paradox543.malankaraorthodoxliturgica.data.translations.di

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.SyncResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.RawTranslationsSource
import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.TranslationBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.TranslationSource
import com.paradox543.malankaraorthodoxliturgica.data.translations.repository.TranslationsRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val translationsDataModule =
    module {
        single { TranslationBundledContentSource(get()) } bind DomainBundledContentSource::class

        single<ResourceTextReader>(named("TranslationResourceReader")) { get<SyncResourceTextReader>() }

        single<RawTranslationsSource> {
            TranslationSource(
                reader = get(named("TranslationResourceReader")),
                json = get(),
            )
        }

        single<TranslationsRepository> {
            TranslationsRepositoryImpl(source = get())
        }
    }
