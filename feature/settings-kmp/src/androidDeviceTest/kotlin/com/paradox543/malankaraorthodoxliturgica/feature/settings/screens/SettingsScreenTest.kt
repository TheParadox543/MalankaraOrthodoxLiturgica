package com.paradox543.malankaraorthodoxliturgica.feature.settings.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.paradox543.malankaraorthodoxliturgica.core.platform.ShareService
import com.paradox543.malankaraorthodoxliturgica.core.platform.SoundModeCapability
import com.paradox543.malankaraorthodoxliturgica.core.ui.scaffold.ScaffoldUiState
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppFontScale
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.SoundMode
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.feature.settings.viewmodel.SettingsViewModel
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
    private lateinit var appInfoProvider: AppInfoProvider
    private lateinit var soundModeCapability: SoundModeCapability

    private val languageFlow = MutableStateFlow(AppLanguage.ENGLISH)
    private val fontScaleFlow = MutableStateFlow(AppFontScale.Medium)
    private val songScrollFlow = MutableStateFlow(false)
    private val soundModeFlow = MutableStateFlow(SoundMode.OFF)
    private val soundDelayFlow = MutableStateFlow(30)

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        shareService = mockk(relaxed = true)
        soundModeCapability = mockk(relaxed = true)

        every { repository.language } returns languageFlow
        every { repository.fontScale } returns fontScaleFlow
        every { repository.songScrollState } returns songScrollFlow
        every { repository.soundMode } returns soundModeFlow
        every { repository.soundRestoreDelay } returns soundDelayFlow

        appInfoProvider = mockk(relaxed = true)
        every { appInfoProvider.versionName } returns "1.0.0"
        every { appInfoProvider.debugMode } returns false

        viewModel =
            SettingsViewModel(
                settingsRepository = repository,
                analyticsService = mockk(relaxed = true),
                soundModeCapability = soundModeCapability,
                appInfoProvider = appInfoProvider,
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
                contentPadding = PaddingValues(),
                onScaffoldStateChanged = {},
            )
        }

        composeTestRule.onNodeWithText("Grant Permission").assertIsDisplayed()
    }

    @Test
    fun togglingSongScroll_updatesViewModel() {
        composeTestRule.setContent {
            SettingsScreen(
                onNavigateToAbout = {},
                requestDndPermission = {},
                settingsViewModel = viewModel,
                shareService = shareService,
                contentPadding = PaddingValues(),
                onScaffoldStateChanged = {},
            )
        }

        // Verify initial state is displayed
        composeTestRule.onNodeWithText("Text Layout for Songs").assertIsDisplayed()

        // Find the switch and toggle it. 
        // In the implementation, the Switch itself is what handles the click.
        // We'll click the text and hope it propagates or find the switch specifically if needed.
        composeTestRule.onNodeWithText("Text Layout for Songs").performClick()

        // Verify that the repository was called to update state.
        // Using coVerify because setSongScrollState is a suspend function.
        coVerify { repository.setSongScrollState(any()) }
    }

    @Test
    fun shareApp_showsBottomSheet() {
        composeTestRule.setContent {
            SettingsScreen(
                onNavigateToAbout = {},
                requestDndPermission = {},
                settingsViewModel = viewModel,
                shareService = shareService,
                contentPadding = PaddingValues(),
                onScaffoldStateChanged = {},
            )
        }

        composeTestRule.onNodeWithText("Share this App").performClick()

        // Verify BottomSheet content appears
        composeTestRule.onNodeWithText("Share link").assertIsDisplayed()
        composeTestRule.onNodeWithText("Generate QR code").assertIsDisplayed()
    }
}
