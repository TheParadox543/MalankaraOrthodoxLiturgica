package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.data.model.BibleDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.data.model.Chapter
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.data.model.Verse
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.AppLanguage

interface BibleRepository {
    fun loadBibleDetails(): List<BibleDetails>

    fun loadBibleChapter(
        bookIndex: Int,
        chapterIndex: Int,
        language: AppLanguage,
    ): Chapter?

    fun loadBibleReading(
        bibleReference: List<BibleReference>,
        language: AppLanguage,
    ): List<Verse>

    fun loadPrefaceTemplates(): PrefaceTemplates
}