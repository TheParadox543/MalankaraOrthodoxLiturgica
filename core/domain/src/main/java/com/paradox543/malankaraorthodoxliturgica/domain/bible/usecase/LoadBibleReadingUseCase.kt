package com.paradox543.malankaraorthodoxliturgica.domain.bible.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReading
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BookNotFoundException
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

/**
 * Use case to load Bible readings given a list of references and a language.
 * It uses the repository to load individual chapters and then selects verse ranges.
 */
class LoadBibleReadingUseCase(
    private val bibleRepository: BibleRepository,
) {
    operator fun invoke(
        bibleReferences: List<BibleReference>,
        language: AppLanguage,
    ): BibleReading {
        val verses = mutableListOf<BibleVerse>()

        try {
            for (reference in bibleReferences) {
                // Convert 1-based book number to 0-based index into cached chapters
                val bookIndex = reference.bookNumber - 1

                for (range in reference.ranges) {
                    // load start chapter
                    val startChapterIndex = range.startChapter - 1
                    val endChapterIndex = range.endChapter - 1

                    val startChapter =
                        bibleRepository.loadBibleChapter(bookIndex, startChapterIndex, language)
                            ?: throw BookNotFoundException("Chapter not found: ${reference.bookNumber}.${range.startChapter}")

                    if (range.startChapter == range.endChapter) {
                        // Take sublist of verses (1-based verse numbers)
                        val fromIndex = range.startVerse - 1
                        val toIndex = range.endVerse // subList end is exclusive, and verses are 0-based in list
                        val sub = startChapter.verses.subList(fromIndex, toIndex)
                        verses.addAll(sub)
                    } else {
                        // take from startVerse to chapter end
                        val fromIndex = range.startVerse - 1
                        val sub1 = startChapter.verses.subList(fromIndex, startChapter.verses.size)
                        verses.addAll(sub1)

                        // load end chapter and take up to endVerse
                        val endChapter =
                            bibleRepository.loadBibleChapter(bookIndex, endChapterIndex, language)
                                ?: throw BookNotFoundException("Chapter not found: ${reference.bookNumber}.${range.endChapter}")
                        val sub2 = endChapter.verses.subList(0, range.endVerse)
                        verses.addAll(sub2)
                    }
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            throw BookNotFoundException("Invalid verse range in the request.")
        }

        return BibleReading(verses = verses)
    }
}
