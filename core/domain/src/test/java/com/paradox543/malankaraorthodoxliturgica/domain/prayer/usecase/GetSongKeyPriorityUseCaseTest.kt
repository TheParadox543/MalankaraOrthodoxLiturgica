package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.TitleStr
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeCalendarRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSongKeyPriorityUseCaseTest {
    @Test
    fun returnsFirstSpecialSongsKeyWhenPresent() =
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
            val repo = FakeCalendarRepository(upcomingEventItems = listOf(details))
            val useCase = GetSongKeyPriorityUseCase(repo)

            val result = useCase()

            assertEquals("specialSongs", result)
        }

    @Test
    fun returnsDefaultWhenNoSpecialSongsKey() =
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
            val repo = FakeCalendarRepository(upcomingEventItems = listOf(details))
            val useCase = GetSongKeyPriorityUseCase(repo)

            val result = useCase()

            assertEquals("default", result)
        }
}
