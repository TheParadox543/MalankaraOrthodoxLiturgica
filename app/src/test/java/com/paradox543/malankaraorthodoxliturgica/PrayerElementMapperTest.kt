package com.paradox543.malankaraorthodoxliturgica

import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toData
import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElementData
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerElementMapperTest {
    @Test
    fun `simple title maps to domain and back`() {
        val data = PrayerElementData.Title("Hello World")
        val domain = data.toDomain()
        assert(domain is PrayerElementDomain.Title)
        assertEquals("Hello World", (domain as PrayerElementDomain.Title).content)

        val roundtrip = domain.toData()
        assert(roundtrip is PrayerElementData.Title)
        assertEquals("Hello World", (roundtrip as PrayerElementData.Title).content)
    }

    @Test
    fun `collapsible block maps nested items`() {
        val nested =
            PrayerElementData.CollapsibleBlock(
                title = "Section",
                items =
                    listOf(
                        PrayerElementData.Heading("H1"),
                        PrayerElementData.Prose("Some text"),
                    ),
            )

        val domain = nested.toDomain()
        assert(domain is PrayerElementDomain.CollapsibleBlock)
        val domainBlock = domain as PrayerElementDomain.CollapsibleBlock
        assertEquals("Section", domainBlock.title)
        assertEquals(2, domainBlock.items.size)
        assert(domainBlock.items[0] is PrayerElementDomain.Heading)
        assert(domainBlock.items[1] is PrayerElementDomain.Prose)

        val back = domain.toData()
        assert(back is PrayerElementData.CollapsibleBlock)
        val backBlock = back as PrayerElementData.CollapsibleBlock
        assertEquals("Section", backBlock.title)
        assertEquals(2, backBlock.items.size)
    }

    @Test
    fun `dynamic songs block maps items and default content`() {
        val songItem =
            PrayerElementData.DynamicSong(
                eventKey = "easter",
                eventTitle = "Easter",
                timeKey = "afterGospel",
                items = listOf(PrayerElementData.Subheading("Verse 1"), PrayerElementData.Song("Alleluia")),
            )

        val block =
            PrayerElementData.DynamicSongsBlock(
                timeKey = "afterGospel",
                items = mutableListOf(songItem),
                defaultContent = songItem,
            )

        val domain = block.toDomain()
        assert(domain is PrayerElementDomain.DynamicSongsBlock)
        val domainBlock = domain as PrayerElementDomain.DynamicSongsBlock
        assertEquals("afterGospel", domainBlock.timeKey)
        assertEquals(1, domainBlock.items.size)
        assertEquals("easter", domainBlock.items[0].eventKey)
        assertEquals("Easter", domainBlock.items[0].eventTitle)
        assertEquals(2, domainBlock.items[0].items.size)
        assert(domainBlock.defaultContent != null)

        val back = domain.toData()
        assert(back is PrayerElementData.DynamicSongsBlock)
        val backBlock = back as PrayerElementData.DynamicSongsBlock
        assertEquals("afterGospel", backBlock.timeKey)
        assertEquals(1, backBlock.items.size)
        assertEquals("easter", backBlock.items[0].eventKey)
        assert(backBlock.defaultContent != null)
    }
}
