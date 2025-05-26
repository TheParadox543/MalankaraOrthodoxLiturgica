package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.model.BibleBook
import com.paradox543.malankaraorthodoxliturgica.model.BibleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleViewModel @Inject constructor(
    private val bibleRepository: BibleRepository
): ViewModel() {
    private val _bibleBooks = MutableStateFlow<List<BibleBook>>(emptyList())
    val bibleBooks: StateFlow<List<BibleBook>> = _bibleBooks

    init {
        viewModelScope.launch { loadBibleBooks() }
    }

    suspend fun loadBibleBooks() {
        try {
            val bibleChapters = bibleRepository.loadBibleChapters()
            _bibleBooks.value = bibleChapters
        } catch (e: Exception) {
            throw e
        }
    }

    fun findBibleBookWithIndex(bookName: String, language: String): Pair<BibleBook?, Int?> {
        val currentBooks = _bibleBooks.value

        currentBooks.forEachIndexed { index, bibleBook ->
            when(language){
                "en" -> {
                    if (bibleBook.book.en == bookName) {
                        return Pair(bibleBook, index)
                    }
                }
                "ml" -> {
                    if (bibleBook.book.ml == bookName) {
                        return Pair(bibleBook, index)
                    }
                }
            }
        }
        return Pair(null, null)
    }

    fun loadBibleChapter(bookNumber: Int, chapterNumber: Int, language: String): Map<String, String> {
        return bibleRepository.loadBibleChapter(bookNumber, chapterNumber, language)
    }

    fun getAdjacentChapters(bookIndex: Int, chapterIndex: Int): Pair<String?, String?> {
        val books = _bibleBooks.value
        val bibleBook = _bibleBooks.value[bookIndex]

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
                // If prevBook.chapters is 0, prevRoute remains null (shouldn't happen with valid data)
            }
            // If prevChapterBookIndex < 0, it means we were at the first book, first chapter. prevRoute remains null.
        } else { // Previous chapter is in the same book
            prevRoute = "bible/$prevChapterBookIndex/$prevChapterIndex"
        }

        // --- Calculate Next Chapter Route ---
        var nextRoute: String? = null
        var nextChapterBookIndex = bookIndex
        var nextChapterIndex = chapterIndex + 1

        if (nextChapterIndex >= bibleBook.chapters) { // Need to go to the next book
            nextChapterBookIndex += 1
            if (nextChapterBookIndex < books.size) { // Check if next book exists
                val nextBook = books[nextChapterBookIndex]
                if (nextBook.chapters > 0) {
                    nextChapterIndex = 0 // First chapter of the next book
                    nextRoute = "bible/$nextChapterBookIndex/$nextChapterIndex"
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