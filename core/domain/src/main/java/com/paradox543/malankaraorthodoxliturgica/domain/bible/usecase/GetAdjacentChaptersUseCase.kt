package com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository

/**
 * Use case to calculate previous and next chapter routes for a given book and chapter index.
 * Returns a Pair(prevRoute, nextRoute) where each may be null if there's no adjacent chapter.
 */
class GetAdjacentChaptersUseCase(
    private val bibleRepository: BibleRepository,
) {
    operator fun invoke(
        bookIndex: Int,
        chapterIndex: Int,
    ): Pair<String?, String?> {
        val books = bibleRepository.loadBibleMetaData()
        if (books.isEmpty()) return Pair(null, null)
        if (bookIndex < 0 || bookIndex >= books.size) return Pair(null, null)

        val bibleBook = books[bookIndex]

        // --- Calculate Previous Chapter Route ---
        var prevRoute: String? = null
        var prevChapterBookIndex = bookIndex
        var prevChapterIndex = chapterIndex - 1

        if (prevChapterIndex < 0) { // Need to go to the previous book
            prevChapterBookIndex -= 1
            if (prevChapterBookIndex >= 0) { // Check if previous book exists
                val prevBook = books[prevChapterBookIndex]
                if (prevBook.chapters > 0) {
                    prevChapterIndex = prevBook.chapters - 1 // Last chapter of previous book
                    prevRoute = "bible/$prevChapterBookIndex/$prevChapterIndex"
                }
                // If prevBook.chapters is 0, prevRoute remains null
            }
            // If prevChapterBookIndex < 0, it means we were at the first book, first chapter. prevRoute remains null.
        } else { // Previous chapter is in the same book
            prevRoute = "bible/$prevChapterBookIndex/$prevChapterIndex"
        }

        // --- Calculate Next Chapter Route ---
        var nextRoute: String? = null
        var nextChapterBookIndex = bookIndex
        val nextChapterIndex = chapterIndex + 1

        if (nextChapterIndex >= bibleBook.chapters) { // Need to go to the next book
            nextChapterBookIndex += 1
            if (nextChapterBookIndex < books.size) { // Check if next book exists
                val nextBook = books[nextChapterBookIndex]
                if (nextBook.chapters > 0) {
                    nextRoute = "bible/$nextChapterBookIndex/0" // First chapter of the next book
                }
                // If nextBook.chapters is 0, nextRoute remains null
            }
            // If nextChapterBookIndex >= books.size, it means we were at the last book, last chapter. nextRoute remains null.
        } else { // Next chapter is in the same book
            nextRoute = "bible/$nextChapterBookIndex/$nextChapterIndex"
        }

        return Pair(prevRoute, nextRoute)
    }
}