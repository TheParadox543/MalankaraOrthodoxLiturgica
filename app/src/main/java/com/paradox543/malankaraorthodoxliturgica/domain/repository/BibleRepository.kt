package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrefaceTemplates

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