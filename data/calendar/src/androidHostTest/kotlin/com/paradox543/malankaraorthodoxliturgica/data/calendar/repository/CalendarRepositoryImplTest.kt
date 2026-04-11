package com.paradox543.malankaraorthodoxliturgica.data.calendar.repository

import com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource.CalendarSource
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.LiturgicalEventDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.calendar.model.TitleStrDto
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalCalendarDates
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
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

    /**
     * Minimal liturgical calendar: April 2025 — Easter on the 20th, Great Lent starts on March 5th.
     */
    private val fakeDates: LiturgicalCalendarDates =
        mapOf(
            "2025" to
                mapOf(
                    "4" to
                        mapOf(
                            "20" to listOf("easter"),
                        ),
                    "3" to
                        mapOf(
                            "5" to listOf("great-lent"),
                        ),
                ),
        )

    private val fakeData =
        mapOf(
            "easter" to easterDto,
            "great-lent" to greatLentDto,
        )

    /**
     * Fresh repository per test — cachedLiturgicalDates and cachedLiturgicalData are `by lazy`.
     */
    @BeforeTest
    fun setup() {
        repository = CalendarRepositoryImpl(source)
    }

    // ─── Cache and error handling ─────────────────────────────────────────────

    @Test
    fun `throws AssetReadException when liturgical dates asset is missing`() =
        runTest {
            coEvery { source.readLiturgicalDates() } throws AssetReadException("not found")
            coEvery { source.readLiturgicalData() } returns fakeData

            assertFailsWith<AssetReadException> {
                repository.checkMonthDataExists(4, 2025)
            }
        }

    @Test
    fun `throws AssetReadException when liturgical data asset is missing`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates
            coEvery { source.readLiturgicalData() } throws AssetReadException("not found")

            assertFailsWith<AssetReadException> {
                repository.loadMonthData(4, 2025)
            }
        }

    // ─── checkMonthDataExists ────────────────────────────────────────────────

    @Test
    fun `checkMonthDataExists returns true when month has data`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates

            assertTrue(repository.checkMonthDataExists(month = 4, year = 2025))
        }

    @Test
    fun `checkMonthDataExists returns false when month has no data`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates

            assertFalse(repository.checkMonthDataExists(month = 6, year = 2025))
        }

    @Test
    fun `checkMonthDataExists returns false for unknown year`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates

            assertFalse(repository.checkMonthDataExists(month = 4, year = 2099))
        }

    // ─── loadMonthData ────────────────────────────────────────────────────────

    @Test
    fun `loadMonthData throws IllegalArgumentException for invalid month 0`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates
            coEvery { source.readLiturgicalData() } returns fakeData

            assertFailsWith<IllegalArgumentException> {
                repository.loadMonthData(month = 0, year = 2025)
            }
        }

    @Test
    fun `loadMonthData throws IllegalArgumentException for invalid month 13`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates
            coEvery { source.readLiturgicalData() } returns fakeData

            assertFailsWith<IllegalArgumentException> {
                repository.loadMonthData(month = 13, year = 2025)
            }
        }

    @Test
    fun `loadMonthData returns weeks where each week starts on Sunday`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates
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
            coEvery { source.readLiturgicalDates() } returns fakeDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val weeks = repository.loadMonthData(month = 4, year = 2025)

            weeks.forEach { week ->
                assertEquals(7, week.days.size)
            }
        }

    @Test
    fun `loadMonthData includes every day of the requested month`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates
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
            coEvery { source.readLiturgicalDates() } returns fakeDates
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
            coEvery { source.readLiturgicalDates() } returns fakeDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val weeks = repository.loadMonthData(month = 4, year = 2025)
            val noEventDay = weeks.flatMap { it.days }.first { it.date == KotlinLocalDate(2025, 4, 1) }

            assertEquals(emptyList(), noEventDay.events)
        }

    @Test
    fun `loadMonthData throws IllegalArgumentException when event key is missing from data store`() =
        runTest {
            // Calendar references a key that is not in the data store
            val missingKeyDates: LiturgicalCalendarDates =
                mapOf(
                    "2025" to mapOf("4" to mapOf("20" to listOf("unknown-key"))),
                )
            coEvery { source.readLiturgicalDates() } returns missingKeyDates
            coEvery { source.readLiturgicalData() } returns emptyMap()

            assertFailsWith<IllegalArgumentException> {
                repository.loadMonthData(month = 4, year = 2025)
            }
        }

    // ─── getUpcomingWeekEvents ────────────────────────────────────────────────

    @Test
    fun `getUpcomingWeekEvents returns exactly 7 days`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates
            coEvery { source.readLiturgicalData() } returns fakeData

            val result = repository.getUpcomingWeekEvents()

            assertEquals(7, result.size)
        }

    @Test
    fun `getUpcomingWeekEvents starts from today`() =
        runTest {
            coEvery { source.readLiturgicalDates() } returns fakeDates
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
            val datesWithTodayEvents: LiturgicalCalendarDates =
                mapOf(
                    today.year.toString() to
                        mapOf(
                            today.month.number.toString() to
                                mapOf(
                                    today.day.toString() to listOf("easter", "great-lent"),
                                ),
                        ),
                )
            coEvery { source.readLiturgicalDates() } returns datesWithTodayEvents
            coEvery { source.readLiturgicalData() } returns fakeData

            val items = repository.getUpcomingWeekEventItems()

            // At minimum the two events from today must appear
            assertTrue(items.size >= 2)
            assertTrue(items.any { it.title.en == "Easter Sunday" })
            assertTrue(items.any { it.title.en == "Great Lent" })
        }
}
