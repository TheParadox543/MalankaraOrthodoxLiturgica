package com.paradox543.malankaraorthodoxliturgica.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage

    // Hilt will automatically provide the Context and DataStore it needs.
    @Singleton
    @Provides
    fun provideReviewManager(
        @ApplicationContext context: Context,
    ): ReviewManager = ReviewManagerFactory.create(context)

    @Singleton
    @Provides
    fun provideAppUpdateManager(
        @ApplicationContext context: Context,
    ): AppUpdateManager = AppUpdateManagerFactory.create(context)

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context,
    ): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun providePlayer(
        @ApplicationContext context: Context,
    ): Player =
        ExoPlayer
            .Builder(context)
            // Optional: You can customize the player here if needed
            // .setAudioAttributes(...)
            // .setHandleAudioBecomingNoisy(true)
            .build()
}
