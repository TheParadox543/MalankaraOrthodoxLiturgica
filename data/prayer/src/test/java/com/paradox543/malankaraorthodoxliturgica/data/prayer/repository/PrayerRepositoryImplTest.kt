package com.paradox543.malankaraorthodoxliturgica.data.prayer.repository

import com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource.PrayerSource
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PageNodeDto
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.runBlocking

class PrayerRepositoryImplTest {

    private val source: PrayerSource = mockk()
    private lateinit var repository: PrayerRepositoryImpl

    @BeforeTest
    fun setup() {
        repository = PrayerRepositoryImpl(source)
    }

    // ─── loadPrayerElements ───────────────────────────────────────────────────

    @Test
    fun `loadPrayerElements delegates to source with correct fileName and language`(): Unit =
        runBlocking {
            coEvery { source.loadPrayerElements(any(), any()) } returns emptyList()

            repository.loadPrayerElements("vespers.json", AppLanguage.ENGLISH)

            coVerify { source.loadPrayerElements("vespers.json", AppLanguage.ENGLISH) }
        }

    @Test
    fun `loadPrayerElements maps returned DTOs to domain elements`(): Unit = runBlocking {
        val dtos = listOf(
            PrayerElementDto.Title("Glory"),
            PrayerElementDto.Prose("Body text"),
        )
        coEvery { source.loadPrayerElements(any(), any()) } returns dtos

        val result = repository.loadPrayerElements("vespers.json", AppLanguage.ENGLISH)

        assertEquals(2, result.size)
        assertIs<PrayerElement.Title>(result[0])
        assertEquals("Glory", (result[0] as PrayerElement.Title).content)
        assertIs<PrayerElement.Prose>(result[1])
    }

    @Test
    fun `loadPrayerElements returns empty list when source returns empty`(): Unit = runBlocking {
        coEvery { source.loadPrayerElements(any(), any()) } returns emptyList()

        val result = repository.loadPrayerElements("empty.json", AppLanguage.MALAYALAM)

        assertEquals(emptyList(), result)
    }

    @Test
    fun `loadPrayerElements applies prayer replacements during mapping`(): Unit = runBlocking {
        coEvery { source.loadPrayerElements(any(), any()) } returns listOf(
            PrayerElementDto.Prose("/tIndented"),
        )

        val result = repository.loadPrayerElements("test.json", AppLanguage.ENGLISH)

        assertEquals("    Indented", (result[0] as PrayerElement.Prose).content)
    }

    // ─── getPrayerNavigationTree ──────────────────────────────────────────────

    @Test
    fun `getPrayerNavigationTree delegates to source with correct language`(): Unit = runBlocking {
        val rootDto = PageNodeDto(route = "root", parent = null)
        coEvery { source.loadPrayerNavigationTree(any()) } returns rootDto

        repository.getPrayerNavigationTree(AppLanguage.ENGLISH)

        coVerify { source.loadPrayerNavigationTree(AppLanguage.ENGLISH) }
    }

    @Test
    fun `getPrayerNavigationTree maps returned PageNodeDto to domain PageNode`(): Unit =
        runBlocking {
            val rootDto = PageNodeDto(
                route = "root",
                type = "section",
                filename = null,
                parent = null,
                children = listOf(
                    PageNodeDto(
                        route = "root/prayers",
                        type = "prayer",
                        filename = "vespers.json",
                        parent = "root",
                    ),
                ),
                languages = listOf("en"),
            )
            coEvery { source.loadPrayerNavigationTree(any()) } returns rootDto

            val result = repository.getPrayerNavigationTree(AppLanguage.ENGLISH)

            assertEquals("root", result.route)
            assertEquals("section", result.type)
            assertEquals(1, result.children.size)
            assertEquals("root/prayers", result.children[0].route)
            assertEquals("vespers.json", result.children[0].filename)
        }

    @Test
    fun `getPrayerNavigationTree maps Malayalam language`(): Unit = runBlocking {
        val rootDto = PageNodeDto(route = "root", parent = null)
        coEvery { source.loadPrayerNavigationTree(AppLanguage.MALAYALAM) } returns rootDto

        repository.getPrayerNavigationTree(AppLanguage.MALAYALAM)

        coVerify(exactly = 1) { source.loadPrayerNavigationTree(AppLanguage.MALAYALAM) }
    }
}
