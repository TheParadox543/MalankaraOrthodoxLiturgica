package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.TitleStr
import com.paradox543.malankaraorthodoxliturgica.domain.repository.CalendarRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class GetSongKeyPriorityUseCaseTest {
    private class FakeCalendarRepository(
        var items: List<LiturgicalEventDetails>,
    ) : CalendarRepository {
        override suspend fun initialize() {}

        override fun getEventsForDate(date: LocalDate): List<LiturgicalEventDetails> = emptyList()

        override fun checkMonthDataExists(
            month: Int,
            year: Int,
        ): Boolean = true

        override fun loadMonthData(
            month: Int?,
            year: Int?,
        ): List<com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeek> = emptyList()

        override fun getUpcomingWeekEvents(): List<com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDay> = emptyList()

        override fun getUpcomingWeekEventItems(): List<LiturgicalEventDetails> = items
    }

    @Test
    fun `returns first specialSongsKey when present`() =
        runBlocking {
            val details =
                LiturgicalEventDetails(
                    type = "type",
                    title = TitleStr(en = "E", ml = null),
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
                LiturgicalEventDetails(
                    type = "type",
                    title = TitleStr(en = "E", ml = null),
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
