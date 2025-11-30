package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.repository.BibleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetAdjacentChaptersUseCaseTest {
    private class FakeRepo(
        private val books: List<BibleBookDetails>,
    ) : BibleRepository {
        override fun loadBibleMetaData(): List<BibleBookDetails> = books

        override fun loadBibleChapter(
            bookIndex: Int,
            chapterIndex: Int,
            language: com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage,
        ) = null

        override fun loadPrefaceTemplates() =
            com.paradox543.malankaraorthodoxliturgica.domain.model.PrefaceTemplates(
                prophets =
                    com.paradox543.malankaraorthodoxliturgica.domain.model
                        .PrefaceContent(emptyList(), emptyList()),
                generalEpistle =
                    com.paradox543.malankaraorthodoxliturgica.domain.model
                        .PrefaceContent(emptyList(), emptyList()),
                paulineEpistle =
                    com.paradox543.malankaraorthodoxliturgica.domain.model
                        .PrefaceContent(emptyList(), emptyList()),
            )
    }

    private fun createUseCase(books: List<BibleBookDetails>) = GetAdjacentChaptersUseCase(FakeRepo(books))

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
        assertEquals("bible/0/1", prev)
        assertEquals("bible/0/3", next)
    }

    @Test
    fun `first chapter of first book has next only`() {
        val books = listOf(simpleBook(3))
        val useCase = createUseCase(books)
        val (prev, next) = useCase(bookIndex = 0, chapterIndex = 0)
        assertNull(prev)
        assertEquals("bible/0/1", next)
    }

    @Test
    fun `last chapter of book with next book`() {
        val books = listOf(simpleBook(2), simpleBook(4))
        val useCase = createUseCase(books)
        val (prev, next) = useCase(bookIndex = 0, chapterIndex = 1)
        assertEquals("bible/0/0", prev)
        assertEquals("bible/1/0", next)
    }

    @Test
    fun `last chapter of last book returns prev only`() {
        val books = listOf(simpleBook(1))
        val useCase = createUseCase(books)
        val (prev, next) = useCase(bookIndex = 0, chapterIndex = 0)
        assertNull(prev)
        assertNull(next)
    }
}
