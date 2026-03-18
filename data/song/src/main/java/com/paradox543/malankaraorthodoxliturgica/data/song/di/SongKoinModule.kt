package com.paradox543.malankaraorthodoxliturgica.data.song.di

import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.paradox543.malankaraorthodoxliturgica.data.song.repository.SongRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.song.repository.SongRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val songDataModule =
    module {
        single<FirebaseStorage> { Firebase.storage }

        single<SongRepository> {
            SongRepositoryImpl(
                context = androidContext(),
                storage = get(),
            )
        }
    }