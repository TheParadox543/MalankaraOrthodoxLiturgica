package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import android.util.Log
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleRoot
import com.paradox543.malankaraorthodoxliturgica.data.model.Chapter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {

    // Lazily load and cache the Bible chapters to avoid re-reading the asset
    private val cachedBibleChapters: List<BibleDetails> by lazy {
        loadJsonAsset<List<BibleDetails>>("bibleBooks.json") ?: emptyList()
    }

    // Generic helper function to load and parse JSON from assets
    private inline fun <reified T> loadJsonAsset(fileName: String): T? {
        return try {
            context.assets.open(fileName).use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                json.decodeFromString<T>(jsonString) // <--- This is where kotlinx.serialization does its magic!
            }
        } catch (e: Exception) {
            Log.e("BibleRepository", "Error loading or parsing $fileName: ${e.message}", e)
            null
        }
    }

    fun loadBibleDetails(): List<BibleDetails> {
        return cachedBibleChapters
    }

    // Cache for loaded chapter files (e.g., bible-ml.json, bible-en.json)
    // Using ConcurrentHashMap for potential thread safety if accessed from multiple coroutines
    private val cachedBibleChapterFiles: ConcurrentHashMap<String, BibleRoot> = ConcurrentHashMap()


    /**
     * Loads a specific Bible chapter's verses from a language-specific JSON file.
     * Caches the entire language file to avoid re-reading for subsequent chapter requests.
     *
     * @param bookIndex The 0-based index of the book within the JSON file.
     * @param chapterIndex The 0-based index of the chapter within the book.
     * @param language The language code (e.g., "ml", "en").
     * @return A map where keys are verse numbers (as String) and values are verse text.
     */
    fun loadBibleChapter(bookIndex: Int, chapterIndex: Int, language: AppLanguage = AppLanguage.MALAYALAM): Chapter? {
        val fileName = "bible-${language.code}.json"

        // Load or retrieve the cached BibleRoot object for the specific language file
        val bibleRoot = cachedBibleChapterFiles.computeIfAbsent(fileName) {
            loadJsonAsset<BibleRoot>(fileName)
                ?: run {
                    Log.e("BibleRepository", "Failed to load Bible file: $fileName")
                    BibleRoot( emptyList()) // Return empty if the file itself can't be loaded
                }
        }

        // Access the book and chapter using safe indexing
        val book = bibleRoot.Book.getOrNull(bookIndex)
        val chapter = book?.Chapter?.getOrNull(chapterIndex)
        return chapter
    }
}