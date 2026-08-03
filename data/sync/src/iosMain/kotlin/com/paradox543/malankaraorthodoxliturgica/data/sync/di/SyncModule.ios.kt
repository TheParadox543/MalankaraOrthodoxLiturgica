package com.paradox543.malankaraorthodoxliturgica.data.sync.di

import com.paradox543.malankaraorthodoxliturgica.data.sync.data.IOSLocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.IOSRemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.LocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.RemoteContentSource
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformSyncModule: Module = module {
    single<LocalContentStore> { IOSLocalContentStore(get()) }
    single<RemoteContentSource> { IOSRemoteContentSource(get(), get()) }
}
