package com.paradox543.malankaraorthodoxliturgica.data.sync.di

import com.paradox543.malankaraorthodoxliturgica.data.sync.data.CompositeBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.ContentResolverImpl
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.SynchronizerImpl
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.SyncResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.BundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.ContentResolver
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.Synchronizer
import com.paradox543.malankaraorthodoxliturgica.domain.sync.ContentUpdateSignal
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlinx.serialization.json.Json

val syncKoinModule = module {
    single<Json> { 
        Json { 
            ignoreUnknownKeys = true 
            coerceInputValues = true
        } 
    }
    single<BundledContentSource> { CompositeBundledContentSource(getAll(), get()) }
    single<ContentResolver> { ContentResolverImpl(get(), get()) }
    single<Synchronizer> { SynchronizerImpl(get(), get(), get(), get()) } bind ContentUpdateSignal::class
    single<SyncResourceTextReader> { SyncResourceTextReader(get()) }
}

expect val platformSyncModule: Module
