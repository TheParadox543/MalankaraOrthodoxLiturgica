package com.paradox543.malankaraorthodoxliturgica.data.bible.repository

import com.paradox543.malankaraorthodoxliturgica.data.bible.datasource.BibleSource
import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toBibleDetailsDomain
import com.paradox543.malankaraorthodoxliturgica.data.bible.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetParsingException
import com.paradox543.malankaraorthodoxliturgica.data.core.exceptions.AssetReadException
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.bible.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.bible.repository.BibleRepository
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepositoryImpl @Inject constructor(
    val source: BibleSource,
) : BibleRepository {
    // Lazily load and cache the Bible meta-data to avoid re-reading the asset.
    // Asset exceptions are translated to BibleParsingException at this boundary.
    private val cachedBibleMetaData: List<BibleBookDetails> by lazy {
        try {
            source.readBibleDetails().toBibleDetailsDomain()
        } catch (e: AssetReadException) {
            throw BibleParsingException("Missing Bible metadata.", e)
        } catch (e: AssetParsingException) {
            throw BibleParsingException("Invalid Bible metadata.", e)
        }
    }

    private val cachedPrefaceTemplates: PrefaceTemplates by lazy {
        try {
            source.readPrefaceTemplates().toDomain()
        } catch (e: AssetReadException) {
            throw BibleParsingException("Missing preface templates.", e)
        } catch (e: AssetParsingException) {
            throw BibleParsingException("Invalid preface templates.", e)
        }
    }

    override fun loadBibleMetaData(): List<BibleBookDetails> = cachedBibleMetaData

    /**
     * Loads a specific Bible chapter from its JSON file.
     *
     * @param bookIndex The 0-based index of the book within the JSON file.
     * @param chapterIndex The 0-based index of the chapter within the book.
     * @param language The language code (e.g., "ml", "en").
     * @return A [BibleChapter] with the parsed verse content.
     */
    override fun loadBibleChapter(
        bookIndex: Int,
        chapterIndex: Int,
        language: AppLanguage,
    ): BibleChapter {
        val bibleLanguage = language.properLanguageMapper()
        val bookFolder = cachedBibleMetaData[bookIndex].folder
        val path = "$bibleLanguage/bible/$bookFolder/${"%03d".format(chapterIndex + 1)}.json"
        return try {
            source.readBibleChapter(path).toDomain()
        } catch (e: AssetReadException) {
            throw BibleParsingException("Missing Bible chapter: $path", e)
        } catch (e: AssetParsingException) {
            throw BibleParsingException("Invalid Bible chapter: $path", e)
        }
    }

    /**
     * Gets the localized name of a Bible book.
     * @param bookIndex The numerical index of the book.
     * @param language The desired [AppLanguage] for the book name.
     * @return The localized book name, or "Error" if not found.
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