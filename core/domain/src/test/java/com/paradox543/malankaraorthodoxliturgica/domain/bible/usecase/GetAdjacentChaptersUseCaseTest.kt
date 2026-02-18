package com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.fakes.FakeBibleRepository
import org.junit.Assert
import org.junit.Test

class GetAdjacentChaptersUseCaseTest {
    private fun createUseCase(books: List<BibleBookDetails>): GetAdjacentChaptersUseCase {
        val bibleRepository = FakeBibleRepository(meta = books)
        return GetAdjacentChaptersUseCase(bibleRepository)
    }

    private fun simpleBook(chapters: Int) =
        BibleBookDetails(
            book = BibleBookName("", ""),
            folder = "",
            chapters = chapters,
            verseCount = emptyList(),
        )

    @Test
    fun `middle chapter returns prev and next within same book`() {
        val books = listOf(simpleBook(5))
        val useCase = createUseCase(books)
        val (prev, next) = useCase(bookIndex = 0, chapterIndex = 2)
        Assert.assertEquals("bible/0/1", prev)
        Assert.assertEquals("bible/0/3", next)
    }

    @Test
    fun `first chapter of first book has next only`() {
        val books = listOf(simpleBook(3))
        val useCase = createUseCase(books)
        val (prev, next) = useCase(bookIndex = 0, chapterIndex = 0)
        Assert.assertNull(prev)
        Assert.assertEquals("bible/0/1", next)
    }

    @Test
    fun `last chapter of book with next book`() {
        val books = listOf(simpleBook(2), simpleBook(4))
        val useCase = createUseCase(books)
        val (prev, next) = useCase(bookIndex = 0, chapterIndex = 1)
        Assert.assertEquals("bible/0/0", prev)
        Assert.assertEquals("bible/1/0", next)
    }

    @Test
    fun `last chapter of last book returns prev only`() {
        val books = listOf(simpleBook(1))
        val useCase = createUseCase(books)
        val (prev, next) = useCase(bookIndex = 0, chapterIndex = 0)
        Assert.assertNull(prev)
        Assert.assertNull(next)
    }
}