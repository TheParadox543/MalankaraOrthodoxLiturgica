package com.paradox543.malankaraorthodoxliturgica.domain.prayer.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakePrayerRepository
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ResolveDynamicSongUseCaseTest {

    @Test
    fun `resolves special song when file exists`() = runBlocking {
        val key = "easterSongs"
        val title = "Easter"
        val timeKey = "afterGospel"
        val elementsMap = mapOf(
            "sacraments/qurbana/qurbanaSongs/easter/afterGospel.json" to listOf(PrayerElement.Song("Easter Song"))
        )
        val prayerRepo = FakePrayerRepository(elementsMap = elementsMap)
        val useCase = ResolveDynamicSongUseCase(prayerRepo)

        val resolved = useCase(AppLanguage.ENGLISH, key, title, timeKey)

        assertNotNull(resolved)
        assertEquals("easterSongs", resolved.eventKey)
        assertEquals("Easter", resolved.eventTitle)
        assertEquals(1, resolved.items.size)
        assertEquals("Easter Song", (resolved.items[0] as PrayerElement.Song).content)
    }

    @Test
    fun `returns null when song file is empty or not found`() = runBlocking {
        val key = "missingSongs"
        val title = "Missing"
        val prayerRepo = FakePrayerRepository(elementsMap = emptyMap())
        val useCase = ResolveDynamicSongUseCase(prayerRepo)

        val resolved = useCase(AppLanguage.ENGLISH, key, title, "afterGospel")

        assertNull(resolved)
    }
}
