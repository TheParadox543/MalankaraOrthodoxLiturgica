package com.paradox543.malankaraorthodoxliturgica.data.settings.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.rules.TemporaryFolder
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for [SettingsRepositoryImpl].
 *
 * A real [DataStore] backed by a temporary file is used (via [PreferenceDataStoreFactory]) so
 * that [DataStore.edit] works correctly. The temp directory is cleaned up after every test.
 * All tests run synchronously via [runTest] + [UnconfinedTestDispatcher].
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsRepositoryImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val tempFolder = TemporaryFolder().also { it.create() }

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: SettingsRepositoryImpl

    @BeforeTest
    fun setup() {
        dataStore =
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { tempFolder.newFile("test_settings.preferences_pb") },
            )
        repository = SettingsRepositoryImpl(dataStore)
    }

    @AfterTest
    fun tearDown() {
        tempFolder.delete()
    }

    // ─── language ────────────────────────────────────────────────────────────

    @Test
    fun `language defaults to MALAYALAM when no value stored`() =
        testScope.runTest {
            assertEquals(AppLanguage.MALAYALAM, repository.language.first())
        }

    @Test
    fun `setLanguage persists ENGLISH and language flow emits ENGLISH`() =
        testScope.runTest {
            repository.setLanguage(AppLanguage.ENGLISH)

            assertEquals(AppLanguage.ENGLISH, repository.language.first())
        }

    @Test
    fun `setLanguage persists MANGLISH and language flow emits MANGLISH`() =
        testScope.runTest {
            repository.setLanguage(AppLanguage.MANGLISH)

            assertEquals(AppLanguage.MANGLISH, repository.language.first())
        }

    @Test
    fun `setLanguage persists INDIC and language flow emits INDIC`() =
        testScope.runTest {
            repository.setLanguage(AppLanguage.INDIC)

            assertEquals(AppLanguage.INDIC, repository.language.first())
        }

    @Test
    fun `language falls back to MALAYALAM when stored code is unrecognised`() =
        testScope.runTest {
            // Write a bogus code directly into the raw DataStore
            dataStore.edit { it[stringPreferencesKey("selected_language")] = "xx" }

            assertEquals(AppLanguage.MALAYALAM, repository.language.first())
        }

    // ─── onboardingCompleted ─────────────────────────────────────────────────

    @Test
    fun `onboardingCompleted defaults to false when no value stored`() =
        testScope.runTest {
            assertFalse(repository.onboardingCompleted.first())
        }

    @Test
    fun `setOnboardingCompleted true makes flow emit true`() =
        testScope.runTest {
            repository.setOnboardingCompleted(true)

            assertTrue(repository.onboardingCompleted.first())
        }

    @Test
    fun `setOnboardingCompleted false makes flow emit false`() =
        testScope.runTest {
            repository.setOnboardingCompleted(true)
            repository.setOnboardingCompleted(false)

            assertFalse(repository.onboardingCompleted.first())
        }

    // ─── fontScale ───────────────────────────────────────────────────────────

    @Test
    fun `fontScale defaults to Medium when no value stored`() =
        testScope.runTest {
            assertEquals(AppFontScale.Medium, repository.fontScale.first())
        }

    @Test
    fun `setFontScale persists Large and fontScale flow emits Large`() =
        testScope.runTest {
            repository.setFontScale(AppFontScale.Large)

            assertEquals(AppFontScale.Large, repository.fontScale.first())
        }

    @Test
    fun `setFontScale persists VerySmall and fontScale flow emits VerySmall`() =
        testScope.runTest {
            repository.setFontScale(AppFontScale.VerySmall)

            assertEquals(AppFontScale.VerySmall, repository.fontScale.first())
        }

    @Test
    fun `fontScale resolves to nearest enum for slightly off stored float`() =
        testScope.runTest {
            // Store a float fractionally close to Small (0.8f) — should still resolve to Small
            dataStore.edit { it[floatPreferencesKey("font_scale")] = 0.81f }

            assertEquals(AppFontScale.Small, repository.fontScale.first())
        }

    // ─── songScrollState ─────────────────────────────────────────────────────

    @Test
    fun `songScrollState defaults to false when no value stored`() =
        testScope.runTest {
            assertFalse(repository.songScrollState.first())
        }

    @Test
    fun `setSongScrollState true makes flow emit true`() =
        testScope.runTest {
            repository.setSongScrollState(true)

            assertTrue(repository.songScrollState.first())
        }

    @Test
    fun `setSongScrollState false makes flow emit false`() =
        testScope.runTest {
            repository.setSongScrollState(true)
            repository.setSongScrollState(false)

            assertFalse(repository.songScrollState.first())
        }

    // ─── soundMode ───────────────────────────────────────────────────────────

    @Test
    fun `soundMode defaults to OFF when no value stored`() =
        testScope.runTest {
            assertEquals(SoundMode.OFF, repository.soundMode.first())
        }

    @Test
    fun `setSoundMode SILENT makes flow emit SILENT`() =
        testScope.runTest {
            repository.setSoundMode(SoundMode.SILENT)

            assertEquals(SoundMode.SILENT, repository.soundMode.first())
        }

    @Test
    fun `setSoundMode DND makes flow emit DND`() =
        testScope.runTest {
            repository.setSoundMode(SoundMode.DND)

            assertEquals(SoundMode.DND, repository.soundMode.first())
        }

    @Test
    fun `setSoundMode OFF makes flow emit OFF`() =
        testScope.runTest {
            repository.setSoundMode(SoundMode.SILENT)
            repository.setSoundMode(SoundMode.OFF)

            assertEquals(SoundMode.OFF, repository.soundMode.first())
        }

    @Test
    fun `soundMode falls back to OFF for unrecognised stored string`() =
        testScope.runTest {
            dataStore.edit { it[stringPreferencesKey("sound_mode")] = "UNKNOWN" }

            assertEquals(SoundMode.OFF, repository.soundMode.first())
        }

    // ─── soundRestoreDelay ───────────────────────────────────────────────────

    @Test
    fun `soundRestoreDelay defaults to 30 when no value stored`() =
        testScope.runTest {
            assertEquals(30, repository.soundRestoreDelay.first())
        }

    @Test
    fun `setSoundRestoreDelay persists value and flow emits it`() =
        testScope.runTest {
            repository.setSoundRestoreDelay(60)

            assertEquals(60, repository.soundRestoreDelay.first())
        }

    @Test
    fun `setSoundRestoreDelay can store zero`() =
        testScope.runTest {
            repository.setSoundRestoreDelay(0)

            assertEquals(0, repository.soundRestoreDelay.first())
        }
}
