package com.paradox543.malankaraorthodoxliturgica.data.repositoryImpl

import com.paradox543.malankaraorthodoxliturgica.data.datasource.BibleSource
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toBibleDetailsDomain
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleParsingException
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.repository.BibleRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepositoryImpl @Inject constructor(
    val source: BibleSource,
) : BibleRepository {
    // Lazily load and cache the Bible chapters to avoid re-reading the asset
    private val cachedBibleMetaData: List<BibleBookDetails> by lazy {
        source.readBibleDetails()?.toBibleDetailsDomain() ?: throw BibleParsingException("Missing Bible metadata.")
    }

    private val cachedPrefaceTemplates: PrefaceTemplates by lazy {
        (
            source.readPrefaceTemplates() ?: throw BibleParsingException("Missing preface templates.")
        ).toDomain()
    }

    override fun loadBibleMetaData(): List<BibleBookDetails> = cachedBibleMetaData

    /**
     * Loads a specific Bible chapter from its JSON file.
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
        val bibleLanguage = language.properLanguageMapper()
        val bookFolder = cachedBibleMetaData[bookIndex].folder
        val path = "$bibleLanguage/bible/$bookFolder/${"%03d".format(chapterIndex + 1)}.json"
        val content = source.readBibleChapter(path)
        return content?.toDomain() ?: throw BibleParsingException("Missing Bible chapter: $path")
    }

    /**
     * Gets the localized name of a Bible book.
     * @param bookIndex The numerical index of the book.
     * @param language The desired AppLanguage for the book name.
     * @return The localized book name, or "Unknown Book" if not found.
     */
    override fun getBibleBookName(
        bookIndex: Int,
        language: AppLanguage,
    ): String {
        val book =
            cachedBibleMetaData.getOrNull(bookIndex)
                ?: return "Error"
        return book.book.get(language)
    }

    override fun loadPrefaceTemplates() = cachedPrefaceTemplates
}