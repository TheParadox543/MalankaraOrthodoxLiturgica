package com.paradox543.malankaraorthodoxliturgica.data.bible.repository

import com.paradox543.malankaraorthodoxliturgica.data.bible.datasource.BibleSource
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookNameDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleChapterDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleParsingException
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleVerseDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceContentDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceTemplatesDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.ProseDto
import com.paradox543.malankaraorthodoxliturgica.domain.prayer.model.PrayerElement
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BibleRepositoryImplTest {
    private val source: BibleSource = mockk()
    private lateinit var repository: BibleRepositoryImpl

    // ─── Shared fixture data ─────────────────────────────────────────────────

    private val fakePrefaceContent =
        PrefaceContentDto(
            en = listOf(ProseDto(type = "prose", content = "EN preface")),
            ml = listOf(ProseDto(type = "prose", content = "ML preface")),
        )

    private val fakePrefaceTemplatesDto =
        PrefaceTemplatesDto(
            prophets = fakePrefaceContent,
            generalEpistle = fakePrefaceContent,
            paulineEpistle = fakePrefaceContent,
        )

    private val fakeBookDetailsDto =
        BibleBookDetailsDto(
            book = BibleBookNameDto(en = "Genesis", ml = "ഉൽപ്പത്തി"),
            folder = "genesis",
            chapters = 50,
            verseCount = listOf(31, 25, 24),
        )

    private val fakeChapterDto =
        BibleChapterDto(
            book = "Genesis",
            chapter = 1,
            verses =
                listOf(
                    BibleVerseDto(id = 1, verse = "In the beginning"),
                    BibleVerseDto(id = 2, verse = "And the earth was void"),
                ),
        )

    /**
     * Creates a fresh repository instance before every test.
     * This is critical because [BibleRepositoryImpl.cachedBibleMetaData] and
     * [BibleRepositoryImpl.cachedPrefaceTemplates] are `by lazy` — the mock
     * stubs must be configured before the first access, and each test should
     * start with an un-initialized cache.
     */
    @BeforeTest
    fun setup() {
        repository = BibleRepositoryImpl(source)
    }

    // ─── loadBibleMetaData ───────────────────────────────────────────────────

    @Test
    fun `loadBibleMetaData returns mapped domain list`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)

        val result = repository.loadBibleMetaData()

        assertEquals(1, result.size)
        assertEquals("Genesis", result[0].book.en)
        assertEquals("genesis", result[0].folder)
        assertEquals(50, result[0].chapters)
    }

    @Test
    fun `loadBibleMetaData caches result and only calls source once`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)

        repository.loadBibleMetaData()
        repository.loadBibleMetaData()

        verify(exactly = 1) { source.readBibleDetails() }
    }

    @Test
    fun `loadBibleMetaData throws BibleParsingException when source returns null`() {
        every { source.readBibleDetails() } returns null

        assertFailsWith<BibleParsingException> {
            repository.loadBibleMetaData()
        }
    }

    @Test
    fun `loadBibleMetaData maps empty list correctly`() {
        every { source.readBibleDetails() } returns emptyList()

        val result = repository.loadBibleMetaData()

        assertEquals(emptyList<Any>(), result)
    }

    // ─── loadBibleChapter ────────────────────────────────────────────────────

    @Test
    fun `loadBibleChapter calls source with correct path for English`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)
        every { source.readBibleChapter("en/bible/genesis/001.json") } returns fakeChapterDto

        repository.loadBibleChapter(bookIndex = 0, chapterIndex = 0, language = AppLanguage.ENGLISH)

        verify { source.readBibleChapter("en/bible/genesis/001.json") }
    }

    @Test
    fun `loadBibleChapter calls source with correct path for Malayalam`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)
        every { source.readBibleChapter("ml/bible/genesis/001.json") } returns fakeChapterDto

        repository.loadBibleChapter(bookIndex = 0, chapterIndex = 0, language = AppLanguage.MALAYALAM)

        verify { source.readBibleChapter("ml/bible/genesis/001.json") }
    }

    @Test
    fun `loadBibleChapter zero-pads chapter index to 3 digits`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)
        // Chapter 10 → zero-padded to "010"
        every { source.readBibleChapter("en/bible/genesis/010.json") } returns fakeChapterDto

        repository.loadBibleChapter(bookIndex = 0, chapterIndex = 9, language = AppLanguage.ENGLISH)

        verify { source.readBibleChapter("en/bible/genesis/010.json") }
    }

    @Test
    fun `loadBibleChapter returns correctly mapped BibleChapter`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)
        every { source.readBibleChapter(any()) } returns fakeChapterDto

        val result =
            repository.loadBibleChapter(
                bookIndex = 0,
                chapterIndex = 0,
                language = AppLanguage.ENGLISH,
            )

        assertEquals("Genesis", result.book)
        assertEquals(1, result.chapter)
        assertEquals(2, result.verses.size)
        assertEquals("In the beginning", result.verses[0].verse)
    }

    @Test
    fun `loadBibleChapter throws BibleParsingException when source returns null`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)
        every { source.readBibleChapter(any()) } returns null

        assertFailsWith<BibleParsingException> {
            repository.loadBibleChapter(
                bookIndex = 0,
                chapterIndex = 0,
                language = AppLanguage.ENGLISH,
            )
        }
    }

    // ─── getBibleBookName ────────────────────────────────────────────────────

    @Test
    fun `getBibleBookName returns English name for AppLanguage ENGLISH`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)

        val result = repository.getBibleBookName(bookIndex = 0, language = AppLanguage.ENGLISH)

        assertEquals("Genesis", result)
    }

    @Test
    fun `getBibleBookName returns Malayalam name for AppLanguage MALAYALAM`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)

        val result = repository.getBibleBookName(bookIndex = 0, language = AppLanguage.MALAYALAM)

        assertEquals("ഉൽപ്പത്തി", result)
    }

    @Test
    fun `getBibleBookName returns Error string when bookIndex is out of range`() {
        every { source.readBibleDetails() } returns listOf(fakeBookDetailsDto)

        val result = repository.getBibleBookName(bookIndex = 99, language = AppLanguage.ENGLISH)

        assertEquals("Error", result)
    }

    // ─── loadPrefaceTemplates ────────────────────────────────────────────────

    @Test
    fun `loadPrefaceTemplates returns correctly mapped templates`() {
        every { source.readPrefaceTemplates() } returns fakePrefaceTemplatesDto

        val result = repository.loadPrefaceTemplates()

        assertEquals("EN preface", (result.prophets.en[0] as PrayerElement.Prose).content)
        assertEquals("ML preface", (result.generalEpistle.ml[0] as PrayerElement.Prose).content)
        assertEquals("EN preface", (result.paulineEpistle.en[0] as PrayerElement.Prose).content)
    }

    @Test
    fun `loadPrefaceTemplates caches result and only calls source once`() {
        every { source.readPrefaceTemplates() } returns fakePrefaceTemplatesDto

        repository.loadPrefaceTemplates()
        repository.loadPrefaceTemplates()

        verify(exactly = 1) { source.readPrefaceTemplates() }
    }

    @Test
    fun `loadPrefaceTemplates throws BibleParsingException when source returns null`() {
        every { source.readPrefaceTemplates() } returns null

        assertFailsWith<BibleParsingException> {
            repository.loadPrefaceTemplates()
        }
    }
}
