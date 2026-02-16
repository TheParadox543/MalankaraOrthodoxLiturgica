package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetailsData
import com.paradox543.malankaraorthodoxliturgica.data.model.TitleStrData
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarDay
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.CalendarWeek
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase.GetSongKeyPriorityUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetSongKeyPriorityUseCaseTest {
    private class FakeCalendarRepository(
        var items: List<LiturgicalEventDetailsData>,
    ) : CalendarRepository {
        override suspend fun initialize() {}

        override fun getEventsForDate(date: LocalDate): List<LiturgicalEventDetailsData> = emptyList()

        override fun checkMonthDataExists(
            month: Int,
            year: Int,
        ): Boolean = true

        override fun loadMonthData(
            month: Int?,
            year: Int?,
        ): List<CalendarWeek> = emptyList()

        override fun getUpcomingWeekEvents(): List<CalendarDay> = emptyList()

        override fun getUpcomingWeekEventItems(): List<LiturgicalEventDetailsData> = items
    }

    @Test
    fun `returns first specialSongsKey when present`() =
        runBlocking {
            val details =
                LiturgicalEventDetailsData(
                    type = "type",
                    title = TitleStrData(en = "E", ml = null),
                    specialSongsKey = "specialSongs",
                    bibleReadings = null,
                    niram = null,
                    startedYear = null,
                )
            val repo = FakeCalendarRepository(listOf(details))
            val useCase = GetSongKeyPriorityUseCase(repo)

            val result = useCase()

            assertEquals("specialSongs", result)
        }

    @Test
    fun `returns default when no specialSongsKey`() =
        runBlocking {
            val details =
                LiturgicalEventDetailsData(
                    type = "type",
                    title = TitleStrData(en = "E", ml = null),
                    specialSongsKey = null,
                    bibleReadings = null,
                    niram = null,
                    startedYear = null,
                )
            val repo = FakeCalendarRepository(listOf(details))
            val useCase = GetSongKeyPriorityUseCase(repo)

            val result = useCase()

            assertEquals("default", result)
        }
}
