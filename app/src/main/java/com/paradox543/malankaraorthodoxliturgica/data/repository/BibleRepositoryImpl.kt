package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import android.util.Log
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toBibleDetailsDomain
import com.paradox543.malankaraorthodoxliturgica.data.mapping.toDomain
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleChapterData
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceContentData
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceTemplatesData
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleBookDetails
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleChapter
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.domain.model.BibleVerse
import com.paradox543.malankaraorthodoxliturgica.domain.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.domain.repository.BibleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) : BibleRepository {
    // Lazily load and cache the Bible chapters to avoid re-reading the asset
    private val cachedBibleChapters: List<BibleBookDetails> by lazy {
        Log.d("BibleRepository", "Caching Bible Details")
        val content = loadJsonAsset<List<BibleDetails>>("bibleBookMetadata.json")?.toBibleDetailsDomain() ?: emptyList()
        Log.d("BibleRepository", "Cached Bible Details: $content")
        content
    }

    private val cachedPrefaceTemplates: PrefaceTemplates by lazy {
        (
            loadJsonAsset<PrefaceTemplatesData>("bible_preface_templates.json") ?: PrefaceTemplatesData(
                prophets = PrefaceContentData(emptyList(), emptyList()),
                generalEpistle = PrefaceContentData(emptyList(), emptyList()),
                paulineEpistle = PrefaceContentData(emptyList(), emptyList()),
            )
        ).toDomain()
    }

    // Generic helper function to load and parse JSON from assets
    private inline fun <reified T> loadJsonAsset(fileName: String): T? =
        try {
            context.assets.open(fileName).use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                json.decodeFromString<T>(jsonString)
            }
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error loading or parsing $fileName: ${e.message}", e)
            null
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
    ): BibleChapter? {
        val bibleLanguage =
            when (language) {
                AppLanguage.MALAYALAM -> "ml"
                else -> "en"
            }
        val bibleBook = cachedBibleChapters[bookIndex]
        Log.d("BibleRepository", "Book: $bibleBook")
        val bookFolder = cachedBibleChapters[bookIndex].folder
        Log.d("BibleRepository", "BookFolder: $bookFolder")
        val fileName = "$bibleLanguage/bible/$bookFolder/${"%03d".format(chapterIndex + 1)}.json"

        return loadJsonAsset<BibleChapterData>(fileName)?.toDomain()
    }

    override fun loadPrefaceTemplates() = cachedPrefaceTemplates
}