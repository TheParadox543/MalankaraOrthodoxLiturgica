package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.data.model.LiturgicalEventDetailsData
import com.paradox543.malankaraorthodoxliturgica.data.model.TitleStrData
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.repository.CalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.repository.PrayerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDynamicSongsUseCaseTest {
    private class FakePrayerRepository(
        private val map: Map<String, List<PrayerElementDomain>>,
    ) : PrayerRepository {
        override suspend fun loadPrayerElements(
            fileName: String,
            language: AppLanguage,
            currentDepth: Int,
        ): List<PrayerElementDomain> = map[fileName] ?: emptyList()
    }

    private class FakeCalendarRepository(
        private val items: List<LiturgicalEventDetailsData>,
    ) : CalendarRepository {
        override suspend fun initialize() {}

        override fun getEventsForDate(date: java.time.LocalDate): List<LiturgicalEventDetailsData> = emptyList()

        override fun checkMonthDataExists(
            month: Int,
            year: Int,
        ): Boolean = true

        override fun loadMonthData(
            month: Int?,
            year: Int?,
        ): List<com.paradox543.malankaraorthodoxliturgica.data.model.CalendarWeek> = emptyList()

        override fun getUpcomingWeekEvents(): List<com.paradox543.malankaraorthodoxliturgica.data.model.CalendarDay> = emptyList()

        override fun getUpcomingWeekEventItems(): List<LiturgicalEventDetailsData> = items
    }

    @Test
    fun `resolves default link and event songs`() =
        runBlocking {
            val defaultSong =
                PrayerElementDomain.DynamicSong(
                    eventKey = "defaultKey",
                    eventTitle = "Default",
                    timeKey = "afterGospel",
                    items = listOf(PrayerElementDomain.Song("Alleluia")),
                )

            val map =
                mapOf(
                    "qurbanaSongs/defaultKey/afterGospel.json" to listOf(PrayerElementDomain.Song("Alleluia")),
                )

            val prayerRepo = FakePrayerRepository(map)

            val eventDetail =
                LiturgicalEventDetailsData(
                    type = "t",
                    title =
                        TitleStrData(en = "E"),
                    specialSongsKey = "defaultKey",
                    bibleReadings = null,
                    niram = null,
                    startedYear = null,
                )

            val calendarRepo = FakeCalendarRepository(listOf(eventDetail))
            val usecase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)

            val block =
                PrayerElementDomain.DynamicSongsBlock(
                    timeKey = "afterGospel",
                    items = mutableListOf(),
                    defaultContent = defaultSong,
                )

            val resolved = usecase(AppLanguage.ENGLISH, block)

            // Should add event song
            assertEquals(true, resolved.items.any { it is PrayerElementDomain.DynamicSong && it.eventKey == "defaultKey" })
        }
}
