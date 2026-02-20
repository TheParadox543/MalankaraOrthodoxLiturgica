package com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeBibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class FormatGospelEntryUseCaseTest {
    private fun makeBook(en: String, ml: String) = BibleBookDetails(
        book = BibleBookName(en = en, ml = ml),
        folder = en.lowercase(),
        chapters = 28,
        verseCount = listOf(25),
    )

    private fun makeUseCase(books: List<BibleBookDetails>): FormatGospelEntryUseCase {
        val repo = FakeBibleRepository(meta = books)
        val rangeUseCase = FormatBibleRangeUseCase()
        val readingEntryUseCase = FormatBibleReadingEntryUseCase(repo, rangeUseCase)
        return FormatGospelEntryUseCase(readingEntryUseCase)
    }

    @Test
    fun `returns empty string for empty list`() {
        val useCase = makeUseCase(emptyList())
        assertEquals("", useCase(emptyList(), AppLanguage.ENGLISH))
    }

    @Test
    fun `formats single gospel entry`() {
        val books = listOf(makeBook("Matthew", "മത്തായി"))
        val useCase = makeUseCase(books)

        val entries = listOf(
            BibleReference(
                bookNumber = 1,
                ranges = listOf(ReferenceRange(startChapter = 5, startVerse = 1, endChapter = 5, endVerse = 12)),
            ),
        )
        assertEquals("Matthew 5:1-12", useCase(entries, AppLanguage.ENGLISH))
    }

    @Test
    fun `formats multiple gospel entries joined by comma`() {
        val books = listOf(
            makeBook("Matthew", "മത്തായി"),
            makeBook("John", "യോഹന്നാൻ"),
        )
        val useCase = makeUseCase(books)

        val entries = listOf(
            BibleReference(
                bookNumber = 1,
                ranges = listOf(ReferenceRange(startChapter = 5, startVerse = 1, endChapter = 5, endVerse = 5)),
            ),
            BibleReference(
                bookNumber = 2,
                ranges = listOf(ReferenceRange(startChapter = 3, startVerse = 16, endChapter = 3, endVerse = 16)),
            ),
        )
        assertEquals("Matthew 5:1-5, John 3:16", useCase(entries, AppLanguage.ENGLISH))
    }

    @Test
    fun `formats gospel entry with Malayalam book name`() {
        val books = listOf(makeBook("Matthew", "മത്തായി"))
        val useCase = makeUseCase(books)

        val entries = listOf(
            BibleReference(
                bookNumber = 1,
                ranges = listOf(ReferenceRange(startChapter = 5, startVerse = 1, endChapter = 5, endVerse = 12)),
            ),
        )
        assertEquals("മത്തായി 5:1-12", useCase(entries, AppLanguage.MALAYALAM))
    }
}
