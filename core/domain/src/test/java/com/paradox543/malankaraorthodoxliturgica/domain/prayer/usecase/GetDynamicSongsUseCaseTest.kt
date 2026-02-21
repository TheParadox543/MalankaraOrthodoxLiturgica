package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.LiturgicalEventDetails
import com.paradox543.malankaraorthodoxliturgica.domain.calendar.model.TitleStr
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeCalendarRepository
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakePrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GetDynamicSongsUseCaseTest {
    private fun makeBlock(
        timeKey: String = "afterGospel",
        defaultContent: PrayerElement.DynamicSong? = null,
    ) = PrayerElement.DynamicSongsBlock(
        timeKey = timeKey,
        items = mutableListOf(),
        defaultContent = defaultContent,
    )

    @Test
    fun `resolves default link and event songs`() =
        runBlocking {
            val defaultSong =
                PrayerElement.DynamicSong(
                    eventKey = "defaultKey",
                    eventTitle = "Default",
                    timeKey = "afterGospel",
                    items = listOf(PrayerElement.Song("Alleluia")),
                )

            val elementsMap =
                mapOf(
                    "qurbanaSongs/defaultKey/afterGospel.json" to listOf(PrayerElement.Song("Alleluia")),
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
            val resolved = usecase(AppLanguage.ENGLISH, makeBlock(defaultContent = defaultSong))

            assertTrue(resolved.items.any { it is PrayerElement.DynamicSong && it.eventKey == "defaultKey" })
        }

    @Test
    fun `adds default content directly when it contains no Link`() =
        runBlocking {
            val defaultSong =
                PrayerElement.DynamicSong(
                    eventKey = "default",
                    eventTitle = "Default",
                    timeKey = "afterGospel",
                    items = listOf(PrayerElement.Song("Alleluia")),
                )
            val prayerRepo = FakePrayerRepository()
            val calendarRepo = FakeCalendarRepository()
            val usecase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)

            val resolved = usecase(AppLanguage.ENGLISH, makeBlock(defaultContent = defaultSong))

            assertTrue(resolved.items.any { it is PrayerElement.DynamicSong && it.eventKey == "default" })
        }

    @Test
    fun `adds departed faithful song when not already in event list`() =
        runBlocking {
            val elementsMap =
                mapOf(
                    "qurbanaSongs/allDepartedFaithful/afterGospel.json" to listOf(PrayerElement.Song("Departed song")),
                )
            val prayerRepo = FakePrayerRepository(elementsMap = elementsMap)
            val calendarRepo = FakeCalendarRepository()
            val usecase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)

            val resolved = usecase(AppLanguage.ENGLISH, makeBlock())

            assertTrue(resolved.items.any { it is PrayerElement.DynamicSong && it.eventKey == "allDepartedFaithful" })
        }

    @Test
    fun `skips departed faithful song when already in event list`() =
        runBlocking {
            val alreadyPresent =
                PrayerElement.DynamicSong(
                    eventKey = "allDepartedFaithful",
                    eventTitle = "Departed",
                    timeKey = "afterGospel",
                    items = listOf(PrayerElement.Song("Song")),
                )
            val block =
                PrayerElement.DynamicSongsBlock(
                    timeKey = "afterGospel",
                    items = mutableListOf(alreadyPresent),
                    defaultContent = null,
                )
            val elementsMap =
                mapOf(
                    "qurbanaSongs/allDepartedFaithful/afterGospel.json" to listOf(PrayerElement.Song("Departed song")),
                )
            val prayerRepo = FakePrayerRepository(elementsMap = elementsMap)
            val calendarRepo = FakeCalendarRepository()
            val usecase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)

            val resolved = usecase(AppLanguage.ENGLISH, block)

            assertEquals(1, resolved.items.count { it is PrayerElement.DynamicSong && it.eventKey == "allDepartedFaithful" })
        }

    @Test
    fun `uses Malayalam title for events when language is MALAYALAM`() =
        runBlocking {
            // GetDynamicSongsUseCase calls specialSongsKey.removeSuffix("Songs") when building the path,
            // so "christmasSongs" → "christmas" in the filename
            val elementsMap =
                mapOf(
                    "qurbanaSongs/christmas/afterGospel.json" to listOf(PrayerElement.Song("Song")),
                )
            val eventDetail =
                LiturgicalEventDetails(
                    type = "feast",
                    title = TitleStr(en = "Christmas", ml = "ക്രിസ്മസ്"),
                    specialSongsKey = "christmasSongs",
                )
            val prayerRepo = FakePrayerRepository(elementsMap = elementsMap)
            val calendarRepo = FakeCalendarRepository(upcomingEventItems = listOf(eventDetail))
            val usecase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)

            val resolved = usecase(AppLanguage.MALAYALAM, makeBlock())

            val song =
                resolved.items
                    .filterIsInstance<PrayerElement.DynamicSong>()
                    .firstOrNull { it.eventKey == "christmasSongs" }
            assertEquals("ക്രിസ്മസ്", song?.eventTitle)
        }

    @Test
    fun `skips events without specialSongsKey`() =
        runBlocking {
            val eventDetail =
                LiturgicalEventDetails(
                    type = "feast",
                    title = TitleStr(en = "Generic Feast"),
                    specialSongsKey = null,
                )
            val prayerRepo = FakePrayerRepository()
            val calendarRepo = FakeCalendarRepository(upcomingEventItems = listOf(eventDetail))
            val usecase = GetDynamicSongsUseCase(prayerRepo, calendarRepo)

            val resolved = usecase(AppLanguage.ENGLISH, makeBlock())

            assertFalse(resolved.items.any { it is PrayerElement.DynamicSong && it.eventKey == "Generic Feast" })
        }
}
