package com.paradox543.malankaraorthodoxliturgica.domain.fakes

import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookName
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.PrefaceContent
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

/**
 * Test fake for [BibleRepository].
 *
 * Configure constructor parameters in tests to control returned metadata,
 * chapters and book name lookups.
 */
class FakeBibleRepository(
    private val meta: List<BibleBookDetails> = emptyList(),
    private val chapters: Map<Pair<Int, Int>, BibleChapter> = emptyMap(),
    private val bookNames: Map<Int, BibleBookName>? = null,
    private val prefaceTemplates: PrefaceTemplates =
        PrefaceTemplates(
            prophets = PrefaceContent(emptyList(), emptyList()),
            generalEpistle = PrefaceContent(emptyList(), emptyList()),
            paulineEpistle = PrefaceContent(emptyList(), emptyList()),
        ),
) : BibleRepository {
    override fun loadBibleMetaData(): List<BibleBookDetails> = meta

    override fun loadBibleChapter(
        bookIndex: Int,
        chapterIndex: Int,
        language: AppLanguage,
    ): BibleChapter? = chapters[Pair(bookIndex, chapterIndex)]

    override fun getBibleBookName(
        bookIndex: Int,
        language: AppLanguage,
    ): String {
        val name = bookNames?.get(bookIndex) ?: meta.getOrNull(bookIndex)?.book
        return name?.get(language) ?: "Unknown Book"
    }

    override fun loadPrefaceTemplates(): PrefaceTemplates = prefaceTemplates
}
