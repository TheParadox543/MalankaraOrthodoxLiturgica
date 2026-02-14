package com.paradox543.malankaraorthodoxliturgica.domain.bible.repository

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

interface BibleRepository {
    fun loadBibleMetaData(): List<BibleBookDetails>

    fun loadBibleChapter(
        bookIndex: Int,
        chapterIndex: Int,
        language: AppLanguage,
    ): BibleChapter?

    /**
     * Gets the localized name of a Bible book.
     * @param bookIndex The numerical index of the book.
     * @param language The desired AppLanguage for the book name.
     * @return The localized book name, or "Unknown Book" if not found.
     */
    fun getBibleBookName(
        bookIndex: Int,
        language: AppLanguage,
    ): String

    fun loadPrefaceTemplates(): PrefaceTemplates
}