package com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeBibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatBibleReadingEntryUseCaseTest {
    private fun makeBook(en: String, ml: String) = BibleBookDetails(
        book = BibleBookName(en = en, ml = ml),
        folder = en.lowercase(),
        chapters = 50,
        verseCount = listOf(31),
    )

    private fun makeUseCase(books: List<BibleBookDetails>): FormatBibleReadingEntryUseCase {
        val repo = FakeBibleRepository(meta = books)
        return FormatBibleReadingEntryUseCase(repo, FormatBibleRangeUseCase())
    }

    @Test
    fun `formats a single verse reference in English`() {
        val books = listOf(makeBook("Genesis", "ഉല്പത്തി"))
        val useCase = makeUseCase(books)

        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 1)),
        )
        assertEquals("Genesis 1:1", useCase(reference, AppLanguage.ENGLISH))
    }

    @Test
    fun `formats a verse range reference in English`() {
        val books = listOf(makeBook("Genesis", "ഉല്പത്തി"))
        val useCase = makeUseCase(books)

        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 10)),
        )
        assertEquals("Genesis 1:1-10", useCase(reference, AppLanguage.ENGLISH))
    }

    @Test
    fun `formats reference with Malayalam book name`() {
        val books = listOf(makeBook("Genesis", "ഉല്പത്തി"))
        val useCase = makeUseCase(books)

        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 1)),
        )
        assertEquals("ഉല്പത്തി 1:1", useCase(reference, AppLanguage.MALAYALAM))
    }

    @Test
    fun `formats reference with multiple ranges joined by comma`() {
        val books = listOf(makeBook("Genesis", "ഉല്പത്തി"))
        val useCase = makeUseCase(books)

        val reference = BibleReference(
            bookNumber = 1,
            ranges = listOf(
                ReferenceRange(startChapter = 1, startVerse = 1, endChapter = 1, endVerse = 5),
                ReferenceRange(startChapter = 2, startVerse = 3, endChapter = 2, endVerse = 7),
            ),
        )
        assertEquals("Genesis 1:1-5, 2:3-7", useCase(reference, AppLanguage.ENGLISH))
    }
}
