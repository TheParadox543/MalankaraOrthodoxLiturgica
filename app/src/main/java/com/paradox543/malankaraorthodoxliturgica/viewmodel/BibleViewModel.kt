package com.paradox543.malankaraorthodoxliturgica.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReading
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.data.model.Chapter
import com.paradox543.malankaraorthodoxliturgica.data.model.PrayerElementData
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceContent
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.data.model.ReferenceRange
import com.paradox543.malankaraorthodoxliturgica.data.model.Verse
import com.paradox543.malankaraorthodoxliturgica.data.repository.BibleRepositoryImpl
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BookNotFoundException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BibleViewModel @Inject constructor(
    private val bibleRepositoryImpl: BibleRepositoryImpl,
) : ViewModel() {
    private val _bibleBooks = MutableStateFlow<List<BibleDetails>>(emptyList())
    val bibleBooks: StateFlow<List<BibleDetails>> = _bibleBooks

    private val _biblePrefaceTemplates =
        MutableStateFlow(
            PrefaceTemplates(
                prophets = PrefaceContent(emptyList(), emptyList()),
                generalEpistle = PrefaceContent(emptyList(), emptyList()),
                paulineEpistle = PrefaceContent(emptyList(), emptyList()),
            ),
        )
    val biblePrefaceTemplates: StateFlow<PrefaceTemplates> = _biblePrefaceTemplates.asStateFlow()

    private val _selectedBibleReference = MutableStateFlow<List<BibleReference>>(listOf())
    val selectedBibleReference: StateFlow<List<BibleReference>> = _selectedBibleReference.asStateFlow()

    init {
        viewModelScope.launch { loadBibleDetails() }
        viewModelScope.launch { loadBiblePrefaceTemplates() }
    }

    private fun loadBibleDetails() {
        try {
            val bibleChapters = bibleRepositoryImpl.loadBibleDetails()
            _bibleBooks.value = bibleChapters
        } catch (e: Exception) {
            throw e
        }
    }

    private fun loadBiblePrefaceTemplates() {
        try {
            val prefaceTemplates = bibleRepositoryImpl.loadPrefaceTemplates()
            _biblePrefaceTemplates.value = prefaceTemplates
        } catch (e: Exception) {
            // Handle error if needed
            throw e
        }
    }

    fun findBibleBookWithIndex(
        bookName: String,
        language: AppLanguage,
    ): Pair<BibleDetails?, Int?> {
        val currentBooks = _bibleBooks.value

        currentBooks.forEachIndexed { index, bibleBook ->
            when (language) {
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

    fun loadBibleChapter(
        bookNumber: Int,
        chapterNumber: Int,
        language: AppLanguage,
    ): Chapter? = bibleRepositoryImpl.loadBibleChapter(bookNumber, chapterNumber, language)

    fun getAdjacentChapters(
        bookIndex: Int,
        chapterIndex: Int,
    ): Pair<String?, String?> {
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

    /**
     * Gets the localized name of a Bible book.
     * @param bookIndex The numerical index of the book.
     * @param language The desired AppLanguage for the book name.
     * @return The localized book name, or "Unknown Book" if not found.
     */
    fun getBookName(
        bookIndex: Int,
        language: AppLanguage,
    ): String {
        try {
            val book = _bibleBooks.value[bookIndex]

            return when (language) {
                AppLanguage.ENGLISH -> book.book.en
                AppLanguage.MALAYALAM -> book.book.ml
                else -> book.book.en
            }
        } catch (_: Exception) {
            System.err.println("Could not find the book")
            return "Error"
        }
    }

    /**
     * Formats a single BibleRange into a string (e.g., "5:1-10" or "3:16 - 4:5").
     * This is a helper function, not exposed directly to UI.
     */
    private fun formatSingleRange(referenceRange: ReferenceRange): String =
        if (referenceRange.startChapter == referenceRange.endChapter) {
            if (referenceRange.startVerse == referenceRange.endVerse) {
                "${referenceRange.startChapter}:${referenceRange.startVerse}"
            } else {
                "${referenceRange.startChapter}:${referenceRange.startVerse}-${referenceRange.endVerse}"
            }
        } else {
            "${referenceRange.startChapter}:${referenceRange.startVerse} - ${referenceRange.endChapter}:${referenceRange.endVerse}"
        }

    /**
     * Formats a complete BibleReadingEntry (a book with its list of ranges) into a readable string.
     * (e.g., "Matthew 5:1-10, 6:1-5")
     * This function uses the currently selected language from the ViewModel's internal state.
     * @param entry The BibleReadingEntry object containing bookNumber and a list of ranges.
     * @return The formatted string for the entire entry.
     */
    fun formatBibleReadingEntry(
        entry: BibleReference,
        language: AppLanguage,
    ): String {
        val bookName = getBookName(entry.bookNumber - 1, language) // Use bookNumber from entry

        val formattedRanges =
            entry.ranges.joinToString(separator = ", ") { range ->
                formatSingleRange(range)
            }

        return "$bookName $formattedRanges"
    }

    fun formatGospelEntry(
        entries: List<BibleReference>,
        language: AppLanguage,
    ): String {
        if (entries.isEmpty()) {
            return ""
        }

        return entries.joinToString(separator = ", ") { entry ->
            formatBibleReadingEntry(entry, language)
        }
    }

    /**
     * Sets the selected BibleReference to be displayed on the BibleReaderScreen.
     * This is called when a user clicks a Bible reading TextButton.
     */
    fun setSelectedBibleReference(reference: List<BibleReference>) {
        _selectedBibleReference.value = reference
    }

    fun loadBiblePreface(
        bibleReference: BibleReference,
        language: AppLanguage,
    ): List<PrayerElementData>? {
        val book = _bibleBooks.value[bibleReference.bookNumber - 1]
        val prefaceContent =
            book.prefaces
                ?: when (book.category) {
                    "prophet" -> _biblePrefaceTemplates.value.prophets
                    "generalEpistle" -> _biblePrefaceTemplates.value.generalEpistle
                    "paulineEpistle" -> _biblePrefaceTemplates.value.paulineEpistle
                    else -> return null
                }

        val sourcePreface: List<PrayerElementData> =
            when (language) {
                AppLanguage.MALAYALAM -> prefaceContent.ml
                AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> prefaceContent.en
            }

        val title =
            when (language) {
                AppLanguage.MALAYALAM -> book.book.ml
                AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> book.book.en
            }
        val displayTitle =
            when (language) {
                AppLanguage.MALAYALAM -> book.displayTitle?.ml ?: ""
                AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> book.displayTitle?.en ?: ""
            }
        val ordinal =
            when (language) {
                AppLanguage.MALAYALAM -> book.ordinal?.ml ?: ""
                AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> book.ordinal?.en ?: ""
            }

        // Use .map to create a new list with the replaced content
        return sourcePreface.map { item ->
            when (item) {
                is PrayerElementData.Prose -> {
                    item.copy(
                        content =
                            item.content
                                .replace("{title}", title)
                                .replace("{displayTitle}", displayTitle)
                                .replace("{ordinal}", ordinal),
                    )
                }
                else -> item
            }
        }
    }

    fun loadBibleReading(
        bibleReferences: List<BibleReference>,
        language: AppLanguage,
    ): BibleReading =
        try {
            val bibleReference = bibleReferences.firstOrNull()
            val preface =
                if (bibleReference != null) {
                    loadBiblePreface(bibleReference, language)
                } else {
                    null
                }
            BibleReading(
                preface = preface,
                verses = bibleRepositoryImpl.loadBibleReading(bibleReferences, language),
            )
        } catch (e: BookNotFoundException) {
            // Handle the case where a book or chapter is not found
            BibleReading(
                verses =
                    listOf(
                        Verse(
                            "Error",
                            "Book or chapter not found: ${e.message}",
                        ),
                    ),
            )
        }
}