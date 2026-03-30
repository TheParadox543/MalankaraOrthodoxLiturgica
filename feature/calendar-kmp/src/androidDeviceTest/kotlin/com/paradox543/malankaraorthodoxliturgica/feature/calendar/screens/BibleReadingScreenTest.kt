package com.paradox543.malankaraorthodoxliturgica.feature.calendar.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReading
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBiblePrefaceUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleReadingEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatGospelEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.LoadBibleReadingUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.usecase.FormatDateTitleUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel.CalendarViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BibleReadingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var calendarRepository: CalendarRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var translationsRepository: TranslationsRepository
    private lateinit var loadBibleReadingUseCase: LoadBibleReadingUseCase
    private lateinit var formatGospelEntryUseCase: FormatGospelEntryUseCase
    private lateinit var formatBiblePrefaceUseCase: FormatBiblePrefaceUseCase
    private lateinit var formatBibleReadingEntryUseCase: FormatBibleReadingEntryUseCase

    private val languageFlow = MutableStateFlow(AppLanguage.ENGLISH)

    @Before
    fun setup() {
        calendarRepository = mockk(relaxed = true)
        settingsRepository = mockk(relaxed = true)
        translationsRepository = mockk(relaxed = true)
        loadBibleReadingUseCase = mockk(relaxed = true)
        formatGospelEntryUseCase = mockk(relaxed = true)
        formatBiblePrefaceUseCase = mockk(relaxed = true)
        formatBibleReadingEntryUseCase = mockk(relaxed = true)

        every { settingsRepository.language } returns languageFlow
        every { calendarRepository.loadMonthData(any(), any()) } returns emptyList()
        every { calendarRepository.checkMonthDataExists(any(), any()) } returns false
        every { calendarRepository.getUpcomingWeekEvents() } returns emptyList()
        coEvery { translationsRepository.loadTranslations(any()) } returns emptyMap()
    }

    @Test
    fun noSelection_showsEmptyStateMessage() {
        val viewModel = createViewModel()

        composeTestRule.setContent {
            BibleReadingScreen(calendarViewModel = viewModel)
        }

        composeTestRule.onNodeWithText("No Bible readings selected.").assertIsDisplayed()
    }

    @Test
    fun selectedReference_loadsAndDisplaysVerseText() {
        val reference = defaultReference()
        val expectedVerseText = "In the beginning was the Word"

        every { formatBibleReadingEntryUseCase(reference, AppLanguage.ENGLISH) } returns "John 1:1"
        every { loadBibleReadingUseCase(listOf(reference), AppLanguage.ENGLISH) } returns
            BibleReading(
                verses = listOf(BibleVerse(1, expectedVerseText)),
            )

        val viewModel = createViewModel()
        viewModel.setSelectedBibleReference(listOf(reference))

        composeTestRule.setContent {
            BibleReadingScreen(calendarViewModel = viewModel)
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeTestRule.onNodeWithText(expectedVerseText).assertIsDisplayed()
                true
            }.getOrDefault(false)
        }
    }

    private fun createViewModel(): CalendarViewModel =
        CalendarViewModel(
            calendarRepository = calendarRepository,
            settingsRepository = settingsRepository,
            translationsRepository = translationsRepository,
            formatDateTitleUseCase = FormatDateTitleUseCase(),
            loadBibleReadingUseCase = loadBibleReadingUseCase,
            formatGospelEntryUseCase = formatGospelEntryUseCase,
            formatBiblePrefaceUseCase = formatBiblePrefaceUseCase,
            formatBibleReadingEntryUseCase = formatBibleReadingEntryUseCase,
            backgroundDispatcher = Dispatchers.Main,
        )

    private fun defaultReference(): BibleReference =
        BibleReference(
            bookNumber = 1,
            ranges =
                listOf(
                    ReferenceRange(
                        startChapter = 1,
                        startVerse = 1,
                        endChapter = 1,
                        endVerse = 1,
                    ),
                ),
        )
}
