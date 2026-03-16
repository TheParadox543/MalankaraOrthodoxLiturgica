package com.paradox543.malankaraorthodoxliturgica.data.song.di

import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.paradox543.malankaraorthodoxliturgica.data.song.repository.SongRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.song.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SongRepositoryModule {
    @Binds
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
    }
}
