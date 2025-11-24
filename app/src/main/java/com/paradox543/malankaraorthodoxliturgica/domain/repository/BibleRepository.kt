package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrefaceTemplates

interface BibleRepository {
    fun loadBibleDetails(): List<BibleBookDetails>

    fun loadBibleChapter(
        bookIndex: Int,
        chapterIndex: Int,
        language: AppLanguage,
    ): BibleChapter?

    fun loadBibleReading(
        bibleReference: List<BibleReference>,
        language: AppLanguage,
    ): List<BibleVerse>

    fun loadPrefaceTemplates(): PrefaceTemplates
}