package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.TitleStr
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeCalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakePrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDynamicSongsUseCaseTest {
    @Test
    fun resolvesDefaultLinkAndEventSongs() =
        runBlocking {
            val defaultSong =
                PrayerElementDomain.DynamicSong(
                    eventKey = "defaultKey",
                    eventTitle = "Default",
                    timeKey = "afterGospel",
                    items = listOf(PrayerElementDomain.Song("Alleluia")),
                )

            val elementsMap =
                mapOf(
                    "qurbanaSongs/defaultKey/afterGospel.json" to listOf(PrayerElementDomain.Song("Alleluia")),
                )

            val prayerRepo = FakePrayerRepository(elementsMap = elementsMap)

            val eventDetail =
                LiturgicalEventDetails(
                    type = "t",
                    title = TitleStr(en = "E"),
                    specialSongsKey = "defaultKey",
                    bibleReadings = null,
                    niram = null,
                    startedYear = null,
                )

            val calendarRepo = FakeCalendarRepository(upcomingEventItems = listOf(eventDetail))
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
