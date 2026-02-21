package com.paradox543.malankaraorthodoxliturgica.data.prayer.mapping

import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

/**
 * Tests for [PrayerElementDto.toDomain] and [PrayerElement.toData].
 *
 * Each sealed subtype is covered with:
 *  - a toDomain conversion asserting the correct domain type and field values
 *  - a toData conversion asserting the correct DTO type and field values
 *
 * Complex types (CollapsibleBlock, DynamicSong, DynamicSongsBlock, AlternativePrayersBlock)
 * additionally verify that nested items are mapped recursively.
 *
 * applyPrayerReplacements is applied during toDomain — the `/t` and `/u200b`
 * expansions are tested via the dedicated [ApplyPrayerReplacementsTest].
 * Here we just verify the replacement *is* applied by checking that `/t` becomes spaces.
 */
class PrayerElementMapperTest {

    // ─── Simple content types ─────────────────────────────────────────────────

    @Test
    fun `Title toDomain maps content and applies replacements`() {
        val dto = PrayerElementDto.Title("/tGreeting")
        val domain = dto.toDomain()
        assertIs<PrayerElement.Title>(domain)
        assertEquals("    Greeting", domain.content)
    }

    @Test
    fun `Title toData maps content unchanged`() {
        val domain = PrayerElement.Title("Hello")
        val dto = domain.toData()
        assertIs<PrayerElementDto.Title>(dto)
        assertEquals("Hello", dto.content)
    }

    @Test
    fun `Heading round-trip`() {
        val dto = PrayerElementDto.Heading("Chapter 1")
        val domain = dto.toDomain()
        assertIs<PrayerElement.Heading>(domain)
        assertEquals("Chapter 1", domain.content)
        val back = domain.toData()
        assertIs<PrayerElementDto.Heading>(back)
        assertEquals("Chapter 1", back.content)
    }

    @Test
    fun `Subheading round-trip`() {
        val dto = PrayerElementDto.Subheading("Section A")
        val domain = dto.toDomain()
        assertIs<PrayerElement.Subheading>(domain)
        assertEquals("Section A", domain.content)
        assertEquals("Section A", (domain.toData() as PrayerElementDto.Subheading).content)
    }

    @Test
    fun `Prose round-trip`() {
        val dto = PrayerElementDto.Prose("Body text")
        val domain = dto.toDomain() as PrayerElement.Prose
        assertEquals("Body text", domain.content)
        assertEquals("Body text", (domain.toData() as PrayerElementDto.Prose).content)
    }

    @Test
    fun `Song round-trip`() {
        val dto = PrayerElementDto.Song("Alleluia")
        val domain = dto.toDomain() as PrayerElement.Song
        assertEquals("Alleluia", domain.content)
        assertEquals("Alleluia", (domain.toData() as PrayerElementDto.Song).content)
    }

    @Test
    fun `Subtext round-trip`() {
        val dto = PrayerElementDto.Subtext("Response")
        val domain = dto.toDomain() as PrayerElement.Subtext
        assertEquals("Response", domain.content)
        assertEquals("Response", (domain.toData() as PrayerElementDto.Subtext).content)
    }

    @Test
    fun `Source round-trip`() {
        val dto = PrayerElementDto.Source("John 3:16")
        val domain = dto.toDomain() as PrayerElement.Source
        assertEquals("John 3:16", domain.content)
        assertEquals("John 3:16", (domain.toData() as PrayerElementDto.Source).content)
    }

    // ─── Button ───────────────────────────────────────────────────────────────

    @Test
    fun `Button toDomain preserves all fields`() {
        val dto = PrayerElementDto.Button(link = "route/target", label = "/tClick me", replace = true)
        val domain = dto.toDomain() as PrayerElement.Button
        assertEquals("route/target", domain.link)
        assertEquals("    Click me", domain.label)   // applyPrayerReplacements applied to label
        assertEquals(true, domain.replace)
    }

    @Test
    fun `Button with null label toDomain keeps null`() {
        val dto = PrayerElementDto.Button(link = "route/target", label = null, replace = false)
        val domain = dto.toDomain() as PrayerElement.Button
        assertNull(domain.label)
    }

    @Test
    fun `Button toData preserves all fields`() {
        val domain = PrayerElement.Button(link = "route/target", label = "Click me", replace = false)
        val dto = domain.toData() as PrayerElementDto.Button
        assertEquals("route/target", dto.link)
        assertEquals("Click me", dto.label)
        assertEquals(false, dto.replace)
    }

    // ─── Link / LinkCollapsible ───────────────────────────────────────────────

    @Test
    fun `Link round-trip`() {
        val dto = PrayerElementDto.Link("common.json")
        val domain = dto.toDomain() as PrayerElement.Link
        assertEquals("common.json", domain.file)
        assertEquals("common.json", (domain.toData() as PrayerElementDto.Link).file)
    }

    @Test
    fun `LinkCollapsible round-trip`() {
        val dto = PrayerElementDto.LinkCollapsible("litanies.json")
        val domain = dto.toDomain() as PrayerElement.LinkCollapsible
        assertEquals("litanies.json", domain.file)
        assertEquals("litanies.json", (domain.toData() as PrayerElementDto.LinkCollapsible).file)
    }

    // ─── CollapsibleBlock ─────────────────────────────────────────────────────

    @Test
    fun `CollapsibleBlock toDomain maps title and recursively maps items`() {
        val dto = PrayerElementDto.CollapsibleBlock(
            title = "Section",
            items = listOf(PrayerElementDto.Prose("Inner text")),
        )
        val domain = dto.toDomain() as PrayerElement.CollapsibleBlock
        assertEquals("Section", domain.title)
        assertEquals(1, domain.items.size)
        assertIs<PrayerElement.Prose>(domain.items[0])
        assertEquals("Inner text", (domain.items[0] as PrayerElement.Prose).content)
    }

    @Test
    fun `CollapsibleBlock toData maps title and recursively maps items`() {
        val domain = PrayerElement.CollapsibleBlock(
            title = "Section",
            items = listOf(PrayerElement.Song("Hymn")),
        )
        val dto = domain.toData() as PrayerElementDto.CollapsibleBlock
        assertEquals("Section", dto.title)
        assertEquals(1, dto.items.size)
        assertIs<PrayerElementDto.Song>(dto.items[0])
    }

    // ─── DynamicSong ─────────────────────────────────────────────────────────

    @Test
    fun `DynamicSong toDomain maps all fields and items`() {
        val dto = PrayerElementDto.DynamicSong(
            eventKey = "easter",
            eventTitle = "Easter",
            timeKey = "qurbanaGospel",
            items = listOf(PrayerElementDto.Subheading("Easter Hymn"), PrayerElementDto.Song("Lyric")),
        )
        val domain = dto.toDomain() as PrayerElement.DynamicSong
        assertEquals("easter", domain.eventKey)
        assertEquals("Easter", domain.eventTitle)
        assertEquals("qurbanaGospel", domain.timeKey)
        assertEquals(2, domain.items.size)
        assertIs<PrayerElement.Subheading>(domain.items[0])
        assertIs<PrayerElement.Song>(domain.items[1])
    }

    @Test
    fun `DynamicSong toData round-trip`() {
        val domain = PrayerElement.DynamicSong(
            eventKey = "christmas",
            eventTitle = "Christmas",
            timeKey = "hoothomo",
            items = listOf(PrayerElement.Song("Noel")),
        )
        val dto = domain.toData() as PrayerElementDto.DynamicSong
        assertEquals("christmas", dto.eventKey)
        assertEquals("hoothomo", dto.timeKey)
        assertEquals(1, dto.items.size)
    }

    // ─── DynamicSongsBlock ───────────────────────────────────────────────────

    @Test
    fun `DynamicSongsBlock toDomain maps timeKey, items and defaultContent`() {
        val songDto = PrayerElementDto.DynamicSong(
            eventKey = "easter", eventTitle = "Easter", timeKey = "afterGospel", items = emptyList(),
        )
        val defaultDto = PrayerElementDto.DynamicSong(
            eventKey = "default", eventTitle = "Default", timeKey = "afterGospel", items = emptyList(),
        )
        val dto = PrayerElementDto.DynamicSongsBlock(
            timeKey = "afterGospel",
            items = mutableListOf(songDto),
            defaultContent = defaultDto,
        )

        val domain = dto.toDomain() as PrayerElement.DynamicSongsBlock
        assertEquals("afterGospel", domain.timeKey)
        assertEquals(1, domain.items.size)
        assertEquals("easter", domain.items[0].eventKey)
        assertEquals("default", domain.defaultContent?.eventKey)
    }

    @Test
    fun `DynamicSongsBlock with null defaultContent`() {
        val dto = PrayerElementDto.DynamicSongsBlock(
            timeKey = "hoothomo",
            items = mutableListOf(),
            defaultContent = null,
        )
        val domain = dto.toDomain() as PrayerElement.DynamicSongsBlock
        assertNull(domain.defaultContent)
    }

    // ─── AlternativeOption / AlternativePrayersBlock ──────────────────────────

    @Test
    fun `AlternativeOption toDomain maps label and recursively maps items`() {
        val dto = PrayerElementDto.AlternativeOption(
            label = "Option A",
            items = listOf(PrayerElementDto.Prose("Text A")),
        )
        val domain = dto.toDomain() as PrayerElement.AlternativeOption
        assertEquals("Option A", domain.label)
        assertEquals(1, domain.items.size)
        assertIs<PrayerElement.Prose>(domain.items[0])
    }

    @Test
    fun `AlternativePrayersBlock toDomain maps title and options`() {
        val dto = PrayerElementDto.AlternativePrayersBlock(
            title = "Choose a Prayer",
            options = listOf(
                PrayerElementDto.AlternativeOption("A", listOf(PrayerElementDto.Prose("Prose A"))),
                PrayerElementDto.AlternativeOption("B", listOf(PrayerElementDto.Song("Song B"))),
            ),
        )
        val domain = dto.toDomain() as PrayerElement.AlternativePrayersBlock
        assertEquals("Choose a Prayer", domain.title)
        assertEquals(2, domain.options.size)
        assertEquals("A", domain.options[0].label)
        assertIs<PrayerElement.Prose>(domain.options[0].items[0])
        assertEquals("B", domain.options[1].label)
        assertIs<PrayerElement.Song>(domain.options[1].items[0])
    }

    @Test
    fun `AlternativePrayersBlock toData maps title and options`() {
        val domain = PrayerElement.AlternativePrayersBlock(
            title = "Choose a Prayer",
            options = listOf(
                PrayerElement.AlternativeOption("A", listOf(PrayerElement.Prose("Prose A"))),
            ),
        )
        val dto = domain.toData() as PrayerElementDto.AlternativePrayersBlock
        assertEquals("Choose a Prayer", dto.title)
        assertEquals(1, dto.options.size)
        assertEquals("A", dto.options[0].label)
        assertIs<PrayerElementDto.Prose>(dto.options[0].items[0])
    }

    // ─── Error ────────────────────────────────────────────────────────────────

    @Test
    fun `Error round-trip`() {
        val dto = PrayerElementDto.Error("File not found.")
        val domain = dto.toDomain() as PrayerElement.Error
        assertEquals("File not found.", domain.content)
        assertEquals("File not found.", (domain.toData() as PrayerElementDto.Error).content)
    }

    // ─── List helpers ─────────────────────────────────────────────────────────

    @Test
    fun `toDomainList maps all elements`() {
        val dtos = listOf(
            PrayerElementDto.Title("T"),
            PrayerElementDto.Prose("P"),
            PrayerElementDto.Song("S"),
        )
        val domains = dtos.toDomainList()
        assertEquals(3, domains.size)
        assertIs<PrayerElement.Title>(domains[0])
        assertIs<PrayerElement.Prose>(domains[1])
        assertIs<PrayerElement.Song>(domains[2])
    }

    @Test
    fun `toDataList maps all elements`() {
        val domains = listOf(
            PrayerElement.Title("T"),
            PrayerElement.Link("f.json"),
        )
        val dtos = domains.toDataList()
        assertEquals(2, dtos.size)
        assertIs<PrayerElementDto.Title>(dtos[0])
        assertIs<PrayerElementDto.Link>(dtos[1])
    }
}
