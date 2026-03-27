package com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SettingsRepository
    private lateinit var analyticsService: AnalyticsService
    private lateinit var soundModeManager: SoundModeManager
    private lateinit var viewModel: SettingsViewModel

    private val languageFlow = MutableStateFlow(AppLanguage.ENGLISH)
    private val fontScaleFlow = MutableStateFlow(AppFontScale.Medium)
    private val songScrollFlow = MutableStateFlow(false)
    private val soundModeFlow = MutableStateFlow(SoundMode.OFF)
    private val soundDelayFlow = MutableStateFlow(30)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)

        // Mock repository flows
        every { repository.language } returns languageFlow
        every { repository.fontScale } returns fontScaleFlow
        every { repository.songScrollState } returns songScrollFlow
        every { repository.soundMode } returns soundModeFlow
        every { repository.soundRestoreDelay } returns soundDelayFlow

        analyticsService = mockk(relaxed = true)
        soundModeManager = mockk(relaxed = true)

        viewModel = SettingsViewModel(repository, analyticsService, soundModeManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setLanguage updates repository and logs analytics`() =
        runTest {
            val language = AppLanguage.MALAYALAM
            viewModel.setLanguage(language)

            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { repository.setLanguage(language) }
            verify { analyticsService.logLanguageSelected(language.name) }
        }

    @Test
    fun `setFontScaleFromSettings updates repository`() =
        runTest {
            val scale = AppFontScale.VeryLarge
            viewModel.setFontScaleFromSettings(scale)

            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { repository.setFontScale(scale) }
        }

    @Test
    fun `setFontScaleDebounced updates repository after delay`() =
        runTest {
            viewModel.setFontScaleDebounced(1) // Increase from Medium to Large

            // Should not be updated immediately
            coVerify(exactly = 0) { repository.setFontScale(any()) }

            advanceTimeBy(301)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { repository.setFontScale(AppFontScale.Large) }
        }

    @Test
    fun `setSongScrollState updates repository`() =
        runTest {
            viewModel.setSongScrollState(true)
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify { repository.setSongScrollState(true) }
        }

    @Test
    fun `refreshDndPermissionStatus updates hasDndPermission state`() =
        runTest {
            every { soundModeManager.checkDndPermission() } returns true

            viewModel.refreshDndPermissionStatus()

            assertTrue(viewModel.hasDndPermission.value)

            every { soundModeManager.checkDndPermission() } returns false
            viewModel.refreshDndPermissionStatus()

            assertFalse(viewModel.hasDndPermission.value)
        }

    @Test
    fun `onShareAppClicked emits to shareApp flow`() =
        runTest {
            var emitted = false
            val job =
                launch {
                    viewModel.shareApp.collect {
                        emitted = true
                    }
                }

            viewModel.onShareAppClicked()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(emitted)
            job.cancel()
        }

    @Test
    fun `setSoundMode updates repository`() =
        runTest {
            viewModel.setSoundMode(SoundMode.SILENT)
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify { repository.setSoundMode(SoundMode.SILENT) }
        }

    @Test
    fun `setSoundRestoreDelay updates repository`() =
        runTest {
            viewModel.setSoundRestoreDelay(45)
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify { repository.setSoundRestoreDelay(45) }
        }
}