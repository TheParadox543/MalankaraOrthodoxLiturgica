package com.paradox543.malankaraorthodoxliturgica.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppReviewManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.InAppUpdateManager
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeManager
import com.paradox543.malankaraorthodoxliturgica.services.InAppReviewManagerImpl
import com.paradox543.malankaraorthodoxliturgica.services.InAppUpdateManagerImpl
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
        single<AppUpdateManager> { AppUpdateManagerFactory.create(androidContext()) }

        single<InAppUpdateManager> {
            InAppUpdateManagerImpl(
                appUpdateManager = get(),
            )
        }

        single<ReviewManager> { ReviewManagerFactory.create(androidContext()) }

        single<DataStore<Preferences>>(REVIEW_DATASTORE_QUALIFIER) {
            PreferenceDataStoreFactory.create(
                produceFile = { androidContext().preferencesDataStoreFile(REVIEW_DATASTORE_NAME) },
            )
        }

        single<InAppReviewManager> {
            InAppReviewManagerImpl(
                reviewManager = get(),
                dataStore = get(),
            )
        }

        single<ShareService> {
            ShareServiceImpl(
                context = androidContext(),
                analyticsService = getHiltBridge(androidContext()).analyticsService(),
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

        single<SoundModeManager> {
            SoundModeManagerImpl(
                soundModeService = get(),
                workManager = get(),
            )
        }
    }