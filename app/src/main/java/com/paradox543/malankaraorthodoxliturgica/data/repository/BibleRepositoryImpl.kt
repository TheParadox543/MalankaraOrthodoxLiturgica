package com.paradox543.malankaraorthodoxliturgica.data.repository

import com.paradox543.malankaraorthodoxliturgica.data.datasource.BibleSource
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toBibleDetailsDomain
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceContentData
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceTemplatesData
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.repository.BibleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepositoryImpl @Inject constructor(
    val source: BibleSource,
) : BibleRepository {
    // Lazily load and cache the Bible chapters to avoid re-reading the asset
    private val cachedBibleChapters: List<BibleBookDetails> by lazy {
        source.readBibleDetails().toBibleDetailsDomain()
    }

    private val cachedPrefaceTemplates: PrefaceTemplates by lazy {
        (
            source.readPrefaceTemplates() ?: PrefaceTemplatesData(
                prophets = PrefaceContentData(emptyList(), emptyList()),
                generalEpistle = PrefaceContentData(emptyList(), emptyList()),
                paulineEpistle = PrefaceContentData(emptyList(), emptyList()),
            )
        ).toDomain()
    }

    override fun loadBibleDetails(): List<BibleBookDetails> = cachedBibleChapters

    /**
     * Loads a specific Bible chapter's verses from a language-specific JSON file.
     * Caches the entire language file to avoid re-reading for subsequent chapter requests.
     *
     * @param bookIndex The 0-based index of the book within the JSON file.
     * @param chapterIndex The 0-based index of the chapter within the book.
     * @param language The language code (e.g., "ml", "en").
     * @return A map where keys are verse numbers (as String) and values are verse text.
     */
    override fun loadBibleChapter(
        bookIndex: Int,
        chapterIndex: Int,
        language: AppLanguage,
    ): BibleChapter {
        val bibleLanguage =
            when (language) {
                AppLanguage.MALAYALAM -> "ml"
                else -> "en"
            }
        val bookFolder = cachedBibleChapters[bookIndex].folder
        val fileName = "$bibleLanguage/bible/$bookFolder/${"%03d".format(chapterIndex + 1)}.json"
        val content = source.readBibleChapter(fileName)
        return content.toDomain()
    }

    override fun loadPrefaceTemplates() = cachedPrefaceTemplates
}