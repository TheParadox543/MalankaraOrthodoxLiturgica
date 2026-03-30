package com.paradox543.malankaraorthodoxliturgica.feature.calendar.viewmodel

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReading
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BookNotFoundException
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBiblePrefaceUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatBibleReadingEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.FormatGospelEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase.LoadBibleReadingUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.usecase.FormatDateTitleUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.SettingsRepository
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

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
        Dispatchers.setMain(testDispatcher)

        calendarRepository = mockk(relaxed = true)
        settingsRepository = mockk(relaxed = true)
        translationsRepository = mockk(relaxed = true)
        loadBibleReadingUseCase = mockk(relaxed = true)
        formatGospelEntryUseCase = mockk(relaxed = true)
        formatBiblePrefaceUseCase = mockk(relaxed = true)
        formatBibleReadingEntryUseCase = mockk(relaxed = true)

        every { settingsRepository.language } returns languageFlow

        // ViewModel init path loads month + upcoming data + translations.
        every { calendarRepository.loadMonthData(any(), any()) } returns emptyList()
        every { calendarRepository.checkMonthDataExists(any(), any()) } returns false
        every { calendarRepository.getUpcomingWeekEvents() } returns emptyList()
        coEvery { translationsRepository.loadTranslations(any()) } returns emptyMap()
        every { formatBiblePrefaceUseCase(any(), any()) } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadSelectedBibleReading success updates reading state`() =
        runTest {
            val viewModel = createViewModel()
            val refs = bibleReferences(bookNumber = 1)
            val expected = BibleReading(verses = listOf(BibleVerse(1, "Blessed are the poor in spirit")))
            val expectedPreface = listOf(PrayerElement.Prose("A reading from the Holy Gospel"))

            every { loadBibleReadingUseCase(refs, AppLanguage.ENGLISH) } returns expected
            every { formatBiblePrefaceUseCase(refs.first(), AppLanguage.ENGLISH) } returns expectedPreface

            viewModel.loadSelectedBibleReading(refs, AppLanguage.ENGLISH)
            testDispatcher.scheduler.advanceUntilIdle()

            val reading = viewModel.selectedBibleReading.value
            assertNotNull(reading)
            assertEquals(expected.verses, reading.verses)
            assertEquals(expectedPreface, reading.preface)
            assertFalse(viewModel.isBibleReadingLoading.value)
            assertNull(viewModel.bibleReadingError.value)
        }

    @Test
    fun `loadSelectedBibleReading with empty references clears state and skips use case`() =
        runTest {
            val viewModel = createViewModel()
            val refs = bibleReferences(bookNumber = 2)

            every {
                loadBibleReadingUseCase(refs, AppLanguage.ENGLISH)
            } returns BibleReading(verses = listOf(BibleVerse(1, "Initial verse")))

            viewModel.loadSelectedBibleReading(refs, AppLanguage.ENGLISH)
            testDispatcher.scheduler.advanceUntilIdle()
            assertNotNull(viewModel.selectedBibleReading.value)

            viewModel.loadSelectedBibleReading(emptyList(), AppLanguage.ENGLISH)
            testDispatcher.scheduler.advanceUntilIdle()

            assertNull(viewModel.selectedBibleReading.value)
            assertNull(viewModel.bibleReadingError.value)
            assertFalse(viewModel.isBibleReadingLoading.value)

            verify(exactly = 1) { loadBibleReadingUseCase(refs, AppLanguage.ENGLISH) }
        }

    @Test
    fun `loadSelectedBibleReading generic failure sets error`() =
        runTest {
            val viewModel = createViewModel()
            val refs = bibleReferences(bookNumber = 3)

            every { loadBibleReadingUseCase(refs, AppLanguage.ENGLISH) } throws IllegalStateException("bad parse")

            viewModel.loadSelectedBibleReading(refs, AppLanguage.ENGLISH)
            testDispatcher.scheduler.advanceUntilIdle()

            assertNull(viewModel.selectedBibleReading.value)
            assertFalse(viewModel.isBibleReadingLoading.value)
            assertTrue(viewModel.bibleReadingError.value?.contains("bad parse") == true)
        }

    @Test
    fun `loadSelectedBibleReading maps BookNotFoundException to fallback verse`() =
        runTest {
            val viewModel = createViewModel()
            val refs = bibleReferences(bookNumber = 4)

            every {
                loadBibleReadingUseCase(refs, AppLanguage.ENGLISH)
            } throws BookNotFoundException("Matthew 999")

            viewModel.loadSelectedBibleReading(refs, AppLanguage.ENGLISH)
            testDispatcher.scheduler.advanceUntilIdle()

            val reading = viewModel.selectedBibleReading.value
            assertNotNull(reading)
            assertEquals(1, reading.verses.size)
            assertEquals(0, reading.verses.first().id)
            assertTrue(reading.verses.first().verse.contains("Book or chapter not found"))
            assertNull(viewModel.bibleReadingError.value)
        }

    @Test
    fun `loadSelectedBibleReading keeps latest request when called back to back`() =
        runTest {
            val viewModel = createViewModel()
            val firstRefs = bibleReferences(bookNumber = 5)
            val secondRefs = bibleReferences(bookNumber = 6)

            every {
                loadBibleReadingUseCase(firstRefs, AppLanguage.ENGLISH)
            } returns BibleReading(verses = listOf(BibleVerse(1, "First result")))
            every {
                loadBibleReadingUseCase(secondRefs, AppLanguage.ENGLISH)
            } returns BibleReading(verses = listOf(BibleVerse(1, "Second result")))

            // StandardTestDispatcher queues both launches; second call cancels the first before execution.
            viewModel.loadSelectedBibleReading(firstRefs, AppLanguage.ENGLISH)
            viewModel.loadSelectedBibleReading(secondRefs, AppLanguage.ENGLISH)
            testDispatcher.scheduler.advanceUntilIdle()

            val reading = viewModel.selectedBibleReading.value
            assertNotNull(reading)
            assertEquals("Second result", reading.verses.first().verse)

            verify(exactly = 0) { loadBibleReadingUseCase(firstRefs, AppLanguage.ENGLISH) }
            verify(exactly = 1) { loadBibleReadingUseCase(secondRefs, AppLanguage.ENGLISH) }
        }

    private fun createViewModel(): CalendarViewModel {
        val viewModel =
            CalendarViewModel(
                calendarRepository = calendarRepository,
                settingsRepository = settingsRepository,
                translationsRepository = translationsRepository,
                formatDateTitleUseCase = FormatDateTitleUseCase(),
                loadBibleReadingUseCase = loadBibleReadingUseCase,
                formatGospelEntryUseCase = formatGospelEntryUseCase,
                formatBiblePrefaceUseCase = formatBiblePrefaceUseCase,
                formatBibleReadingEntryUseCase = formatBibleReadingEntryUseCase,
                backgroundDispatcher = testDispatcher,
            )

        testDispatcher.scheduler.advanceUntilIdle()
        return viewModel
    }

    private fun bibleReferences(bookNumber: Int): List<BibleReference> =
        listOf(
            BibleReference(
                bookNumber = bookNumber,
                ranges =
                    listOf(
                        ReferenceRange(
                            startChapter = 1,
                            startVerse = 1,
                            endChapter = 1,
                            endVerse = 2,
                        ),
                    ),
            ),
        )
}

