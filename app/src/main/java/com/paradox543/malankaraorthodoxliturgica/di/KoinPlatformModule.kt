package com.paradox543.malankaraorthodoxliturgica.di

import android.app.Activity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManager as GoogleAppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.paradox543.malankaraorthodoxliturgica.ActivityHolder
import com.paradox543.malankaraorthodoxliturgica.BuildConfig
import com.paradox543.malankaraorthodoxliturgica.core.platform.AppUpdateManager as PlatformAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.AndroidSoundModeCapability
import com.paradox543.malankaraorthodoxliturgica.core.platform.AndroidUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeCapability
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.info.AndroidAppInfoProvider
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider
import com.paradox543.malankaraorthodoxliturgica.services.AndroidUpdateManagerImpl
import com.paradox543.malankaraorthodoxliturgica.services.InAppReviewManagerImpl
import com.paradox543.malankaraorthodoxliturgica.services.ShareServiceImpl
import com.paradox543.malankaraorthodoxliturgica.services.sound.SoundModeManagerImpl
import com.paradox543.malankaraorthodoxliturgica.services.sound.SoundModeService
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val REVIEW_DATASTORE_NAME = "review_prefs"
private val REVIEW_DATASTORE_QUALIFIER = named("reviewDataStore")

val platformKoinModule =
    module {
        single<AppInfoProvider> {
            AndroidAppInfoProvider(
                versionName = BuildConfig.VERSION_NAME,
                versionCode = BuildConfig.VERSION_CODE.toString(),
                debugMode = BuildConfig.DEBUG,
            )
        }
        single<GoogleAppUpdateManager> { AppUpdateManagerFactory.create(androidContext()) }

        single {
            AndroidUpdateManagerImpl(
                appUpdateManager = get(),
            )
        }

        single<AndroidUpdateManager> { get<AndroidUpdateManagerImpl>() }

        single<PlatformAppUpdateManager> { get<AndroidUpdateManagerImpl>() }

        single<ReviewManager> { ReviewManagerFactory.create(androidContext()) }

        single<DataStore<Preferences>>(REVIEW_DATASTORE_QUALIFIER) {
            PreferenceDataStoreFactory.create(
                produceFile = { androidContext().preferencesDataStoreFile(REVIEW_DATASTORE_NAME) },
            )
        }

        single<() -> Activity?> {
            { ActivityHolder.activity }
        }

        single<InAppReviewManager> {
            InAppReviewManagerImpl(
                reviewManager = get(),
                dataStore = get(),
                activityProvider = get(),
            )
        }

        single<ShareService> {
            ShareServiceImpl(
                context = androidContext(),
                analyticsService = get(),
            )
        }

        single<WorkManager> {
            WorkManager.getInstance(androidContext())
        }

        single<SoundModeService> {
            SoundModeService(
                context = androidContext(),
            )
        }

        single<SoundModeCapability> {
            AndroidSoundModeCapability(
                manager = get(),
            )
        }

        single<SoundModeManager> {
            SoundModeManagerImpl(
                soundModeService = get(),
                workManager = get(),
            )
        }
    }