package com.paradox543.malankaraorthodoxliturgica.data.calendar.repository

import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarSource
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalDayDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalEventDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalYearlyDatesDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.SeasonDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.TitleStrDto
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate as KotlinLocalDate

@OptIn(ExperimentalTime::class)
class CalendarRepositoryImplTest {
    private val source: CalendarSource = mockk()
    private lateinit var repository: CalendarRepositoryImpl

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private val easterDto =
        LiturgicalEventDetailsDto(
            type = "feast",
            title = TitleStrDto(en = "Easter Sunday", ml = "ഉയിർപ്പ്"),
        )

    private val greatLentDto =
        LiturgicalEventDetailsDto(
            type = "fast",
            title = TitleStrDto(en = "Great Lent"),
        )

    private val fakeYearlyDates =
        listOf(
            LiturgicalYearlyDatesDto(
                version = "1",
                liturgicalYear = "2024-25",
                data =
                    mapOf(
                        "2025-04-20" to
                            LiturgicalDayDto(
                                eventKeys = listOf("easter"),
                                season = SeasonDto.RESURRECTION,
                                tune = 1,
                                lent = 0,
                            ),
                        "2025-03-05" to
                            LiturgicalDayDto(
                                eventKeys = listOf("great-lent"),
                                season = SeasonDto.GREAT_LENT,
                                tune = 1,
                                lent = 1,
                            ),
                    ),
            ),
        )

    private val fakeData =
        mapOf<String, LiturgicalEventDetailsDto>(
            "easter" to easterDto,
            "great-lent" to greatLentDto,
        )

    /**
     * Fresh repository per test — cachedLiturgicalDates and cachedLiturgicalData are `by lazy`.
     */
    @BeforeTest
    fun setup() {
        mockkObject(AppLogger)
        every { AppLogger.d(any(), any()) } returns Unit
        every { AppLogger.e(any(), any(), any()) } returns Unit
        repository = CalendarRepositoryImpl(source)
    }

    @kotlin.test.AfterTest
    fun tearDown() {
        unmockkObject(AppLogger)
    }

    // ─── Cache and error handling ─────────────────────────────────────────────

    @Test
    fun `throws AssetReadException when liturgical data asset is missing`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } throws AssetReadException("not found")

            assertFailsWith<AssetReadException> {
                repository.loadMonthData(4, 2025)
            }
        }

    // ─── checkMonthDataExists ────────────────────────────────────────────────

    @Test
    fun `checkMonthDataExists returns true when month has data`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates

            assertTrue(repository.checkMonthDataExists(month = 4, year = 2025))
        }

    @Test
    fun `checkMonthDataExists returns false when month has no data`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates

            assertFalse(repository.checkMonthDataExists(month = 6, year = 2025))
        }

    @Test
    fun `checkMonthDataExists returns false for unknown year`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates

            assertFalse(repository.checkMonthDataExists(month = 4, year = 2099))
        }

    // ─── loadMonthData ────────────────────────────────────────────────────────

    @Test
    fun `loadMonthData throws IllegalArgumentException for invalid month 0`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            assertFailsWith<IllegalArgumentException> {
                repository.loadMonthData(month = 0, year = 2025)
            }
        }

    @Test
    fun `loadMonthData throws IllegalArgumentException for invalid month 13`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            assertFailsWith<IllegalArgumentException> {
                repository.loadMonthData(month = 13, year = 2025)
            }
        }

    @Test
    fun `loadMonthData returns weeks where each week starts on Sunday`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val weeks = repository.loadMonthData(month = 4, year = 2025)

            assertTrue(weeks.isNotEmpty())
            weeks.forEach { week ->
                // Use kotlinx.datetime.DayOfWeek for comparison with the domain date property
                assertEquals(
                    DayOfWeek.SUNDAY,
                    week.days
                        .first()
                        .date.dayOfWeek,
                )
            }
        }

    @Test
    fun `loadMonthData returns weeks where each week has exactly 7 days`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val weeks = repository.loadMonthData(month = 4, year = 2025)

            weeks.forEach { week ->
                assertEquals(7, week.days.size)
            }
        }

    @Test
    fun `loadMonthData includes every day of the requested month`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val weeks = repository.loadMonthData(month = 4, year = 2025)
            val allDates = weeks.flatMap { it.days }.map { it.date }

            // April 2025 has 30 days — all must be present in the result
            val aprilDays = (1..30).map { KotlinLocalDate(2025, 4, it) }
            assertTrue(allDates.containsAll(aprilDays))
        }

    @Test
    fun `loadMonthData maps Easter event to the correct day`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val weeks = repository.loadMonthData(month = 4, year = 2025)
            val easter = weeks.flatMap { it.days }.first { it.date == KotlinLocalDate(2025, 4, 20) }

            assertEquals(1, easter.events.size)
            assertEquals("Easter Sunday", easter.events[0].title.en)
            assertEquals("feast", easter.events[0].type)
        }

    @Test
    fun `loadMonthData returns empty events for days with no liturgical entry`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val weeks = repository.loadMonthData(month = 4, year = 2025)
            val noEventDay = weeks.flatMap { it.days }.first { it.date == KotlinLocalDate(2025, 4, 1) }

            assertEquals(emptyList(), noEventDay.events)
        }

    @Test
    fun `loadMonthData throws IllegalArgumentException when event key is missing from data store`() =
        runTest {
            // Calendar references a key that is not in the data store
            val missingKeyYearlyDates =
                listOf(
                    LiturgicalYearlyDatesDto(
                        version = "1",
                        liturgicalYear = "2024-25",
                        data =
                            mapOf(
                                "2025-04-20" to
                                    LiturgicalDayDto(
                                        eventKeys = listOf("unknown-key"),
                                        season = SeasonDto.RESURRECTION,
                                        tune = 1,
                                        lent = 0,
                                    ),
                            ),
                    ),
                )
            coEvery { source.loadAllYears() } returns missingKeyYearlyDates
            coEvery { source.readLiturgicalData() } returns emptyMap()

            assertFailsWith<IllegalArgumentException> {
                repository.loadMonthData(month = 4, year = 2025)
            }
        }

    // ─── getUpcomingWeekEvents ────────────────────────────────────────────────

    @Test
    fun `getUpcomingWeekEvents returns exactly 7 days`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val result = repository.getUpcomingWeekEvents()

            assertEquals(7, result.size)
        }

    @Test
    fun `getUpcomingWeekEvents starts from today`() =
        runTest {
            coEvery { source.loadAllYears() } returns fakeYearlyDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val today =
                kotlin.time.Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            val result = repository.getUpcomingWeekEvents()

            assertEquals(today, result.first().date)
        }

    // ─── getUpcomingWeekEventItems ────────────────────────────────────────────

    @Test
    fun `getUpcomingWeekEventItems returns flat list of all events across the week`() =
        runTest {
            // Build a calendar where today has two events
            val today =
                kotlin.time.Clock.System
                    .now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            val yearlyDatesWithTodayEvents =
                listOf(
                    LiturgicalYearlyDatesDto(
                        version = "1",
                        liturgicalYear = "2024-25",
                        data =
                            mapOf(
                                today.toString() to
                                    LiturgicalDayDto(
                                        eventKeys = listOf("easter", "great-lent"),
                                        season = SeasonDto.RESURRECTION,
                                        tune = 1,
                                        lent = 0,
                                    ),
                            ),
                    ),
                )
            coEvery { source.loadAllYears() } returns yearlyDatesWithTodayEvents
            coEvery { source.readLiturgicalData() } returns fakeData

            val items = repository.getUpcomingWeekEventItems()

            // At minimum the two events from today must appear
            assertTrue(items.size >= 2)
            assertTrue(items.any { it.title.en == "Easter Sunday" })
            assertTrue(items.any { it.title.en == "Great Lent" })
        }
}
