package com.paradox543.malankaraorthodoxliturgica

import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toData
import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.prayer.model.PrayerElementDto
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerElementMapperTest {
    @Test
    fun `simple title maps to domain and back`() {
        val data = PrayerElementDto.Title("Hello World")
        val domain = data.toDomain()
        assert(domain is PrayerElement.Title)
        assertEquals("Hello World", (domain as PrayerElement.Title).content)

        val roundtrip = domain.toData()
        assert(roundtrip is PrayerElementDto.Title)
        assertEquals("Hello World", (roundtrip as PrayerElementDto.Title).content)
    }

    @Test
    fun `collapsible block maps nested items`() {
        val nested =
            PrayerElementDto.CollapsibleBlock(
                title = "Section",
                items =
                    listOf(
                        PrayerElementDto.Heading("H1"),
                        PrayerElementDto.Prose("Some text"),
                    ),
            )

        val domain = nested.toDomain()
        assert(domain is PrayerElement.CollapsibleBlock)
        val domainBlock = domain as PrayerElement.CollapsibleBlock
        assertEquals("Section", domainBlock.title)
        assertEquals(2, domainBlock.items.size)
        assert(domainBlock.items[0] is PrayerElement.Heading)
        assert(domainBlock.items[1] is PrayerElement.Prose)

        val back = domain.toData()
        assert(back is PrayerElementDto.CollapsibleBlock)
        val backBlock = back as PrayerElementDto.CollapsibleBlock
        assertEquals("Section", backBlock.title)
        assertEquals(2, backBlock.items.size)
    }

    @Test
    fun `dynamic songs block maps items and default content`() {
        val songItem =
            PrayerElementDto.DynamicSong(
                eventKey = "easter",
                eventTitle = "Easter",
                timeKey = "afterGospel",
                items = listOf(PrayerElementDto.Subheading("Verse 1"), PrayerElementDto.Song("Alleluia")),
            )

        val block =
            PrayerElementDto.DynamicSongsBlock(
                timeKey = "afterGospel",
                items = mutableListOf(songItem),
                defaultContent = songItem,
            )

        val domain = block.toDomain()
        assert(domain is PrayerElement.DynamicSongsBlock)
        val domainBlock = domain as PrayerElement.DynamicSongsBlock
        assertEquals("afterGospel", domainBlock.timeKey)
        assertEquals(1, domainBlock.items.size)
        assertEquals("easter", domainBlock.items[0].eventKey)
        assertEquals("Easter", domainBlock.items[0].eventTitle)
        assertEquals(2, domainBlock.items[0].items.size)
        assert(domainBlock.defaultContent != null)

        val back = domain.toData()
        assert(back is PrayerElementDto.DynamicSongsBlock)
        val backBlock = back as PrayerElementDto.DynamicSongsBlock
        assertEquals("afterGospel", backBlock.timeKey)
        assertEquals(1, backBlock.items.size)
        assertEquals("easter", backBlock.items[0].eventKey)
        assert(backBlock.defaultContent != null)
    }
}
