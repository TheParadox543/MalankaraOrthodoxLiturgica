package com.paradox543.malankaraorthodoxliturgica.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleReading
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.model.BookNotFoundException
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrayerElementDomain
import com.paradox543.malankaraorthodoxliturgica.domain.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.FormatBiblePrefaceUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.FormatBibleReadingEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.FormatGospelEntryUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.GetAdjacentChaptersUseCase
import com.paradox543.malankaraorthodoxliturgica.domain.usecase.LoadBibleReadingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BibleViewModel @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val loadBibleReadingUseCase: LoadBibleReadingUseCase,
    private val getAdjacentChaptersUseCase: GetAdjacentChaptersUseCase,
    private val formatBibleReadingEntryUseCase: FormatBibleReadingEntryUseCase,
    private val formatGospelEntryUseCase: FormatGospelEntryUseCase,
    private val formatBiblePrefaceUseCase: FormatBiblePrefaceUseCase,
) : ViewModel() {
    val bibleBooks: List<BibleBookDetails> = bibleRepository.loadBibleMetaData()

    private val _selectedBibleReference = MutableStateFlow<List<BibleReference>>(listOf())
    val selectedBibleReference: StateFlow<List<BibleReference>> = _selectedBibleReference.asStateFlow()

    fun loadBibleBook(bookNumber: Int): BibleBookDetails = bibleBooks[bookNumber]

    fun loadBibleChapter(
        bookNumber: Int,
        chapterNumber: Int,
        language: AppLanguage,
    ): BibleChapter? = bibleRepository.loadBibleChapter(bookNumber, chapterNumber, language)

    fun getAdjacentChapters(
        bookIndex: Int,
        chapterIndex: Int,
    ): Pair<String?, String?> = getAdjacentChaptersUseCase(bookIndex, chapterIndex)

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
    ): String = formatBibleReadingEntryUseCase(entry, language)

    fun formatGospelEntry(
        entries: List<BibleReference>,
        language: AppLanguage,
    ): String = formatGospelEntryUseCase(entries, language)

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
    ): List<PrayerElementDomain>? = formatBiblePrefaceUseCase(bibleReference, language)

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
            loadBibleReadingUseCase(bibleReferences, language).copy(preface = preface)
        } catch (e: BookNotFoundException) {
            // Handle the case where a book or chapter is not found
            BibleReading(
                verses =
                    listOf(
                        BibleVerse(
                            0,
                            "Book or chapter not found: ${e.message}",
                        ),
                    ),
            )
        }
}