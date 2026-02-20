package com.paradox543.malankaraorthodoxliturgica.data.bible.mapping

import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookNameDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleChapterDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleVerseDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.DisplayTextDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceContentDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceTemplatesDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.ProseDto
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElementDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BibleClassesMappersTest {
    // ─── ProseDto ────────────────────────────────────────────────────────────

    @Test
    fun `ProseDto toDomain maps content correctly`() {
        val dto = ProseDto(type = "prose", content = "In the beginning")
        val domain = dto.toDomain()
        assertEquals("In the beginning", domain.content)
    }

    @Test
    fun `List of ProseDto toProseListDomain maps all items`() {
        val dtos =
            listOf(
                ProseDto(type = "prose", content = "First"),
                ProseDto(type = "prose", content = "Second"),
            )
        val result = dtos.toProseListDomain()
        assertEquals(2, result.size)
        assertEquals("First", result[0].content)
        assertEquals("Second", result[1].content)
    }

    @Test
    fun `List of ProseDto toProseListDomain returns empty list for empty input`() {
        assertEquals(emptyList<Any>(), emptyList<ProseDto>().toProseListDomain())
    }

    // ─── PrefaceContentDto ───────────────────────────────────────────────────

    @Test
    fun `PrefaceContentDto toDomain maps en and ml prose lists`() {
        val dto =
            PrefaceContentDto(
                en = listOf(ProseDto(type = "prose", content = "English preface")),
                ml = listOf(ProseDto(type = "prose", content = "Malayalam preface")),
            )
        val domain = dto.toDomain()
        assertEquals(1, domain.en.size)
        assertEquals("English preface", (domain.en[0] as PrayerElementDomain.Prose).content)
        assertEquals(1, domain.ml.size)
        assertEquals("Malayalam preface", (domain.ml[0] as PrayerElementDomain.Prose).content)
    }

    // ─── PrefaceTemplatesDto ─────────────────────────────────────────────────

    @Test
    fun `PrefaceTemplatesDto toDomain maps all three template fields`() {
        fun makeContent(label: String) =
            PrefaceContentDto(
                en = listOf(ProseDto("prose", "$label EN")),
                ml = listOf(ProseDto("prose", "$label ML")),
            )
        val dto =
            PrefaceTemplatesDto(
                prophets = makeContent("Prophets"),
                generalEpistle = makeContent("GenEpistle"),
                paulineEpistle = makeContent("Pauline"),
            )
        val domain = dto.toDomain()
        assertEquals("Prophets EN", (domain.prophets.en[0] as PrayerElementDomain.Prose).content)
        assertEquals("GenEpistle EN", (domain.generalEpistle.en[0] as PrayerElementDomain.Prose).content)
        assertEquals("Pauline EN", (domain.paulineEpistle.en[0] as PrayerElementDomain.Prose).content)
    }

    // ─── DisplayTextDto ──────────────────────────────────────────────────────

    @Test
    fun `DisplayTextDto toDomain maps en and ml when both present`() {
        val dto = DisplayTextDto(en = "Genesis", ml = "ഉൽപ്പത്തി")
        val domain = dto.toDomain()
        assertEquals("Genesis", domain.en)
        assertEquals("ഉൽപ്പത്തി", domain.ml)
    }

    @Test
    fun `DisplayTextDto toDomain maps null ml as null`() {
        val dto = DisplayTextDto(en = "Genesis", ml = null)
        val domain = dto.toDomain()
        assertEquals("Genesis", domain.en)
        assertNull(domain.ml)
    }

    // ─── BibleBookNameDto ────────────────────────────────────────────────────

    @Test
    fun `BibleBookNameDto toDomain maps en and ml`() {
        val dto = BibleBookNameDto(en = "Genesis", ml = "ഉൽപ്പത്തി")
        val domain = dto.toDomain()
        assertEquals("Genesis", domain.en)
        assertEquals("ഉൽപ്പത്തി", domain.ml)
    }

    // ─── BibleVerseDto ───────────────────────────────────────────────────────

    @Test
    fun `BibleVerseDto toDomain maps id and verse`() {
        val dto = BibleVerseDto(id = 3, verse = "And God said, Let there be light")
        val domain = dto.toDomain()
        assertEquals(3, domain.id)
        assertEquals("And God said, Let there be light", domain.verse)
    }

    // ─── BibleChapterDto ─────────────────────────────────────────────────────

    @Test
    fun `BibleChapterDto toDomain maps book, chapter, and verse list`() {
        val dto =
            BibleChapterDto(
                book = "Genesis",
                chapter = 1,
                verses =
                    listOf(
                        BibleVerseDto(id = 1, verse = "In the beginning"),
                        BibleVerseDto(id = 2, verse = "And the earth was void"),
                    ),
            )
        val domain = dto.toDomain()
        assertEquals("Genesis", domain.book)
        assertEquals(1, domain.chapter)
        assertEquals(2, domain.verses.size)
        assertEquals(1, domain.verses[0].id)
        assertEquals("In the beginning", domain.verses[0].verse)
    }

    @Test
    fun `BibleChapterDto toDomain handles empty verse list`() {
        val dto = BibleChapterDto(book = "Genesis", chapter = 1, verses = emptyList())
        assertEquals(emptyList<Any>(), dto.toDomain().verses)
    }

    // ─── BibleBookDetailsDto ─────────────────────────────────────────────────

    @Test
    fun `BibleBookDetailsDto toDomain maps all required fields`() {
        val dto =
            BibleBookDetailsDto(
                book = BibleBookNameDto(en = "Genesis", ml = "ഉൽപ്പത്തി"),
                folder = "genesis",
                chapters = 50,
                verseCount = listOf(31, 25),
            )
        val domain = dto.toDomain()
        assertEquals("Genesis", domain.book.en)
        assertEquals("genesis", domain.folder)
        assertEquals(50, domain.chapters)
        assertEquals(listOf(31, 25), domain.verseCount)
        assertNull(domain.category)
        assertNull(domain.prefaces)
        assertNull(domain.displayTitle)
        assertNull(domain.ordinal)
    }

    @Test
    fun `BibleBookDetailsDto toDomain maps optional fields when present`() {
        val prose =
            PrefaceContentDto(
                en = listOf(ProseDto("prose", "EN")),
                ml = listOf(ProseDto("prose", "ML")),
            )
        val dto =
            BibleBookDetailsDto(
                book = BibleBookNameDto(en = "Isaiah", ml = "യെശയ്യാ"),
                folder = "isaiah",
                chapters = 66,
                verseCount = listOf(31),
                category = "prophets",
                prefaces = prose,
                displayTitle = DisplayTextDto(en = "The Book of Isaiah"),
                ordinal = DisplayTextDto(en = "First", ml = "ഒന്നാമൻ"),
            )
        val domain = dto.toDomain()
        assertEquals("prophets", domain.category)
        assertEquals("EN", (domain.prefaces?.en?.get(0) as? PrayerElementDomain.Prose)?.content)
        assertEquals("The Book of Isaiah", domain.displayTitle?.en)
        assertEquals("First", domain.ordinal?.en)
        assertEquals("ഒന്നാമൻ", domain.ordinal?.ml)
    }

    // ─── List<BibleBookDetailsDto> ───────────────────────────────────────────

    @Test
    fun `List of BibleBookDetailsDto toBibleDetailsDomain maps all items`() {
        val dtos =
            listOf(
                BibleBookDetailsDto(BibleBookNameDto("Genesis", "ഉൽപ്പത്തി"), "genesis", listOf(31), 50),
                BibleBookDetailsDto(BibleBookNameDto("Exodus", "പുറപ്പാട്"), "exodus", listOf(22), 40),
            )
        val result = dtos.toBibleDetailsDomain()
        assertEquals(2, result.size)
        assertEquals("Genesis", result[0].book.en)
        assertEquals("Exodus", result[1].book.en)
    }

    @Test
    fun `List of BibleBookDetailsDto toBibleDetailsDomain returns empty list for empty input`() {
        assertEquals(emptyList<Any>(), emptyList<BibleBookDetailsDto>().toBibleDetailsDomain())
    }

    // ─── Round-trip: domain → data ───────────────────────────────────────────

    @Test
    fun `BibleVerse toData round-trips correctly`() {
        val domain = BibleVerse(id = 7, verse = "And God saw that it was good")
        val dto = domain.toData()
        assertEquals(7, dto.id)
        assertEquals("And God saw that it was good", dto.verse)
    }

    @Test
    fun `BibleChapter toData round-trips correctly`() {
        val domain =
            BibleChapter(
                book = "Genesis",
                chapter = 1,
                verses = listOf(BibleVerse(id = 1, verse = "In the beginning")),
            )
        val dto = domain.toData()
        assertEquals("Genesis", dto.book)
        assertEquals(1, dto.chapter)
        assertEquals(1, dto.verses.size)
        assertEquals(1, dto.verses[0].id)
        assertEquals("In the beginning", dto.verses[0].verse)
    }

    // Helper: verify BibleBookName mapping used in multiple tests
    @Test
    fun `BibleBookName get returns correct value for known language`() {
        val name = BibleBookName(en = "Genesis", ml = "ഉൽപ്പത്തി")
        assertEquals("Genesis", name.en)
        assertEquals("ഉൽപ്പത്തി", name.ml)
    }
}
