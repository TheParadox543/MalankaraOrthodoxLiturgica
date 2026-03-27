package com.paradox543.malankaraorthodoxliturgica.feature.settings.screens

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel
    private lateinit var shareService: ShareService

    private val languageFlow = MutableStateFlow(AppLanguage.ENGLISH)
    private val fontScaleFlow = MutableStateFlow(AppFontScale.Medium)
    private val songScrollFlow = MutableStateFlow(false)
    private val soundModeFlow = MutableStateFlow(SoundMode.OFF)
    private val soundDelayFlow = MutableStateFlow(30)

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        shareService = mockk(relaxed = true)

        every { repository.language } returns languageFlow
        every { repository.fontScale } returns fontScaleFlow
        every { repository.songScrollState } returns songScrollFlow
        every { repository.soundMode } returns soundModeFlow
        every { repository.soundRestoreDelay } returns soundDelayFlow

        viewModel =
            SettingsViewModel(
                settingsRepository = repository,
                analyticsService = mockk(relaxed = true),
                soundModeCapability = mockk(relaxed = true),
                appInfoProvider =
                    mockk(
                        versionName = "1.0.0",
                        versionCode = "1",
                        debugMode = false,
                    ),
            )
    }

    @Test
    fun grantPermissionButton_showsWhenNoPermission() {
        // Set DND permission to false in ViewModel
        viewModel.setDndPermissionStatus(false)

        composeTestRule.setContent {
            SettingsScreen(
                onNavigateToAbout = {},
                requestDndPermission = {},
                settingsViewModel = viewModel,
                shareService = shareService,
            )
        }

        composeTestRule.onNodeWithText("Grant Permission").assertIsDisplayed()
    }

    @Test
    fun togglingSongScroll_updatesViewModel() {
        songScrollFlow.value = true

        composeTestRule.setContent {
            SettingsScreen(
                onNavigateToAbout = {},
                requestDndPermission = {},
                settingsViewModel = viewModel,
                shareService = shareService,
            )
        }

        // Verify initial state is ON based on flow
        composeTestRule.onNodeWithText("Text Layout for Songs").assertIsDisplayed()

        // Find the switch and toggle it
        // Note: In a real test, you might use a testTag for the switch
        composeTestRule.onNodeWithText("Text Layout for Songs").performClick()

        // Verify that the repository was called to update state
        verify { viewModel.setSongScrollState(any()) }
    }

    @Test
    fun shareApp_showsBottomSheet() {
        composeTestRule.setContent {
            SettingsScreen(
                onNavigateToAbout = {},
                requestDndPermission = {},
                settingsViewModel = viewModel,
                shareService = shareService,
            )
        }

        composeTestRule.onNodeWithText("Share this App").performClick()

        // Verify BottomSheet content appears
        composeTestRule.onNodeWithText("Share link").assertIsDisplayed()
        composeTestRule.onNodeWithText("Generate QR code").assertIsDisplayed()
    }
}