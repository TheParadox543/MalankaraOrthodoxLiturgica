package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.data.model.Chapter
import com.paradox543.malankaraorthodoxliturgica.data.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.data.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val settingsRepository: SettingsRepository,
): ViewModel() {
    private val _bibleBooks = MutableStateFlow<List<BibleDetails>>(emptyList())
    val bibleBooks: StateFlow<List<BibleDetails>> = _bibleBooks

    private val selectedLanguage = MutableStateFlow(AppLanguage.MALAYALAM)
    init {
        viewModelScope.launch {
            settingsRepository.selectedLanguage.collect {
                selectedLanguage.value = it
            }
        }
    }

    init {
        viewModelScope.launch { loadBibleDetails() }
    }

    private fun loadBibleDetails() {
        try {
            val bibleChapters = bibleRepository.loadBibleDetails()
            _bibleBooks.value = bibleChapters
        } catch (e: Exception) {
            throw e
        }
    }

    fun findBibleBookWithIndex(bookName: String, language: AppLanguage): Pair<BibleDetails?, Int?> {
        val currentBooks = _bibleBooks.value

        currentBooks.forEachIndexed { index, bibleBook ->
            when(language){
//                AppLanguage.ENGLISH -> {
//                    if (bibleBook.book.en == bookName) {
//                        return Pair(bibleBook, index)
//                    }
//                }

                AppLanguage.MALAYALAM -> {
                    if (bibleBook.book.ml == bookName) {
                        return Pair(bibleBook, index)
                    }
                }

                else -> {
                    if (bibleBook.book.en == bookName) {
                        return Pair(bibleBook, index)
                    }
                }
            }
        }
        return Pair(null, null)
    }

    fun loadBibleChapter(bookNumber: Int, chapterNumber: Int, language: AppLanguage): Chapter? {
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

    fun getBookName(bookIndex: Int): String {
        val book = _bibleBooks.value[bookIndex].book
        val bookName = when (selectedLanguage.value) {
            AppLanguage.ENGLISH -> book.en
            AppLanguage.MALAYALAM -> book.ml
            else -> book.en

        }
        return bookName
    }

    /**
     * Formats a single BibleRange into a string (e.g., "5:1-10" or "3:16 - 4:5").
     * This is a helper function, not exposed directly to UI.
     */
    private fun formatSingleRange(referenceRange: ReferenceRange): String {
        return if (referenceRange.startChapter == referenceRange.endChapter) {
            if (referenceRange.startVerse == referenceRange.endVerse) {
                "${referenceRange.startChapter}:${referenceRange.startVerse}"
            } else {
                "${referenceRange.startChapter}:${referenceRange.startVerse}-${referenceRange.endVerse}"
            }
        } else {
            "${referenceRange.startChapter}:${referenceRange.startVerse} - ${referenceRange.endChapter}:${referenceRange.endVerse}"
        }
    }

    /**
     * Formats a complete BibleReadingEntry (a book with its list of ranges) into a readable string.
     * (e.g., "Matthew 5:1-10, 6:1-5")
     * This function uses the currently selected language from the ViewModel's internal state.
     * @param entry The BibleReadingEntry object containing bookNumber and a list of ranges.
     * @return The formatted string for the entire entry.
     */
    fun formatBibleReadingEntry(entry: BibleReference): String {
        val languageToUse = selectedLanguage.value
        val bookName = getBookName(entry.bookNumber-1) // Use bookNumber from entry

        val formattedRanges = entry.ranges.joinToString(separator = ", ") { range ->
            formatSingleRange(range)
        }

        return "$bookName $formattedRanges"
    }

}