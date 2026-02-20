package com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BookNotFoundException
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeBibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LoadBibleReadingUseCaseTest {
    private fun makeVerses(count: Int) = (1..count).map { BibleVerse(id = it, verse = "Verse $it") }

    private fun makeChapter(bookIndex: Int, chapterIndex: Int, verseCount: Int) =
        Pair(bookIndex, chapterIndex) to BibleChapter(
            book = "Genesis",
            chapter = chapterIndex + 1,
            verses = makeVerses(verseCount),
        )

    private fun makeBook(name: String = "Genesis") = BibleBookDetails(
        book = BibleBookName(en = name, ml = name),
        folder = "genesis",
        chapters = 50,
        verseCount = listOf(31),
    )

    @Test
    fun `loads a single verse from one chapter`() {
        val chapters = mapOf(makeChapter(bookIndex = 0, chapterIndex = 0, verseCount = 31))
        val repo = FakeBibleRepository(meta = listOf(makeBook()), chapters = chapters)
        val useCase = LoadBibleReadingUseCase(repo)

        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 1)),
        )
        val result = useCase(listOf(reference), AppLanguage.ENGLISH)

        assertEquals(1, result.verses.size)
        assertEquals("Verse 1", result.verses[0].verse)
    }

    @Test
    fun `loads a verse range within the same chapter`() {
        val chapters = mapOf(makeChapter(bookIndex = 0, chapterIndex = 0, verseCount = 31))
        val repo = FakeBibleRepository(meta = listOf(makeBook()), chapters = chapters)
        val useCase = LoadBibleReadingUseCase(repo)

        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 3, endChapter = 1, endVerse = 7)),
        )
        val result = useCase(listOf(reference), AppLanguage.ENGLISH)

        assertEquals(5, result.verses.size)
        assertEquals("Verse 3", result.verses[0].verse)
        assertEquals("Verse 7", result.verses[4].verse)
    }

    @Test
    fun `loads a verse range spanning multiple chapters`() {
        val chapters = mapOf(
            makeChapter(bookIndex = 0, chapterIndex = 0, verseCount = 10),
            makeChapter(bookIndex = 0, chapterIndex = 1, verseCount = 10),
        )
        val repo = FakeBibleRepository(meta = listOf(makeBook()), chapters = chapters)
        val useCase = LoadBibleReadingUseCase(repo)

        // Chapter 1 verses 8-10 (3 verses) + Chapter 2 verses 1-3 (3 verses) = 6 total
        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 8, endChapter = 2, endVerse = 3)),
        )
        val result = useCase(listOf(reference), AppLanguage.ENGLISH)

        assertEquals(6, result.verses.size)
        assertEquals("Verse 8", result.verses[0].verse)
        assertEquals("Verse 3", result.verses[5].verse)
    }

    @Test
    fun `loads multiple references and concatenates verses`() {
        val chapters = mapOf(
            makeChapter(bookIndex = 0, chapterIndex = 0, verseCount = 10),
            makeChapter(bookIndex = 0, chapterIndex = 1, verseCount = 10),
        )
        val repo = FakeBibleRepository(meta = listOf(makeBook()), chapters = chapters)
        val useCase = LoadBibleReadingUseCase(repo)

        val references = listOf(
            BibleReference(
                bookNumber = 1,
                ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 2)),
            ),
            BibleReference(
                bookNumber = 1,
                ranges = listOf(ReferenceRange(startChapter = 2, startVerse = 1, endChapter = 2, endVerse = 2)),
            ),
        )
        val result = useCase(references, AppLanguage.ENGLISH)

        assertEquals(4, result.verses.size)
    }

    @Test
    fun `throws BookNotFoundException when chapter is not found`() {
        val repo = FakeBibleRepository(meta = listOf(makeBook()), chapters = emptyMap())
        val useCase = LoadBibleReadingUseCase(repo)

        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 1)),
        )

        assertFailsWith<BookNotFoundException> {
            useCase(listOf(reference), AppLanguage.ENGLISH)
        }
    }

    @Test
    fun `throws BookNotFoundException for invalid verse range`() {
        // Chapter has only 5 verses but we request verse 10
        val chapters = mapOf(makeChapter(bookIndex = 0, chapterIndex = 0, verseCount = 5))
        val repo = FakeBibleRepository(meta = listOf(makeBook()), chapters = chapters)
        val useCase = LoadBibleReadingUseCase(repo)

        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 10)),
        )

        assertFailsWith<BookNotFoundException> {
            useCase(listOf(reference), AppLanguage.ENGLISH)
        }
    }
}
