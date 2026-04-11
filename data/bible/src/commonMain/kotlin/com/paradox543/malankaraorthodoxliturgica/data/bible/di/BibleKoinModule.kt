package com.paradox543.malankaraorthodoxliturgica.data.bible.di

import com.paradox543.malankaraorthodoxliturgica.data.bible.datasource.BibleComposeResourceReader
import com.paradox543.malankaraorthodoxliturgica.data.bible.datasource.BibleSource
import com.paradox543.malankaraorthodoxliturgica.data.bible.repository.BibleRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val bibleDataModule =
    module {
        single<ResourceTextReader>(named("BibleResourceReader")) { BibleComposeResourceReader() }

        single { BibleSource(reader = get(named("BibleResourceReader")), get()) }

        single<BibleRepository> {
            BibleRepositoryImpl(source = get())
        }
    }