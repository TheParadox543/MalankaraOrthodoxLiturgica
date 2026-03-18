package com.paradox543.malankaraorthodoxliturgica.data.bible.di

import com.paradox543.malankaraorthodoxliturgica.data.bible.datasource.BibleSource
import com.paradox543.malankaraorthodoxliturgica.data.bible.repository.BibleRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import org.koin.dsl.module

val bibleDataModule =
    module {
        single { BibleSource(reader = get()) }

        single<BibleRepository> {
            BibleRepositoryImpl(source = get())
        }
    }