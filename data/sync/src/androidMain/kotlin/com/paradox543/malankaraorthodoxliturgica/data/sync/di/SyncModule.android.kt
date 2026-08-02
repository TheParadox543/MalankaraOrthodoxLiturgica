package com.paradox543.malankaraorthodoxliturgica.data.sync.di

import com.google.firebase.storage.FirebaseStorage
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.AndroidLocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.data.FirebaseRemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.LocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.RemoteContentSource
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformSyncModule: Module = module {
    single<LocalContentStore> { AndroidLocalContentStore(get(), get()) }
    single { FirebaseStorage.getInstance() }
    single<RemoteContentSource> { FirebaseRemoteContentSource(get(), get()) }
}
