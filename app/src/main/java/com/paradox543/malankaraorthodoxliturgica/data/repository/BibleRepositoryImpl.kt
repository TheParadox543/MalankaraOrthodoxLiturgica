package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import android.util.Log
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleDetails
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleReference
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleRoot
import com.paradox543.malankaraorthodoxliturgica.data.model.Chapter
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceContent
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceTemplates
import com.paradox543.malankaraorthodoxliturgica.data.model.Verse
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.model.BookNotFoundException
import com.paradox543.malankaraorthodoxliturgica.domain.repository.BibleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) : BibleRepository {
    // Lazily load and cache the Bible chapters to avoid re-reading the asset
    private val cachedBibleChapters: List<BibleDetails> by lazy {
        loadJsonAsset<List<BibleDetails>>("bibleBookMetadata.json") ?: emptyList()
    }

    private val cachedPrefaceTemplates: PrefaceTemplates by lazy {
        loadJsonAsset<PrefaceTemplates>("bible_preface_templates.json") ?: PrefaceTemplates(
            prophets = PrefaceContent(emptyList(), emptyList()),
            generalEpistle = PrefaceContent(emptyList(), emptyList()),
            paulineEpistle = PrefaceContent(emptyList(), emptyList()),
        )
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

    override fun loadBibleDetails(): List<BibleDetails> = cachedBibleChapters

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
    override fun loadBibleChapter(
        bookIndex: Int,
        chapterIndex: Int,
        language: AppLanguage,
    ): Chapter? {
        val bibleLanguage =
            when (language) {
                AppLanguage.MALAYALAM -> "ml"
                else -> "en"
            }
        val fileName = "bible-$bibleLanguage.json"

        // Load or retrieve the cached BibleRoot object for the specific language file
        val bibleRoot: BibleRoot =
            cachedBibleChapterFiles.computeIfAbsent(fileName) {
                loadJsonAsset<BibleRoot>(fileName)
                    ?: run {
                        Log.e("BibleRepository", "Failed to load Bible file: $fileName")
                        BibleRoot(emptyList()) // Return empty if the file itself can't be loaded
                    }
            }

        // Access the book and chapter using safe indexing
        val book = bibleRoot.Book.getOrNull(bookIndex)
        val chapter = book?.Chapter?.getOrNull(chapterIndex)
        return chapter
    }

    override fun loadBibleReading(
        bibleReferences: List<BibleReference>,
        language: AppLanguage,
    ): List<Verse> {
        val bibleLanguage =
            when (language) {
                AppLanguage.MALAYALAM -> "ml"
                else -> "en"
            }
        val fileName = "bible-$bibleLanguage.json"

        val bibleRoot: BibleRoot =
            cachedBibleChapterFiles.computeIfAbsent(fileName) {
                loadJsonAsset<BibleRoot>(fileName)
                    ?: run {
                        Log.e("BibleRepository", "Failed to load Bible file: $fileName")
                        BibleRoot(emptyList()) // Return empty if the file itself can't be loaded
                    }
            }

        val verses = mutableListOf<Verse>()
        try {
            for (bibleReference in bibleReferences) {
                val book = bibleRoot.Book.getOrNull(bibleReference.bookNumber - 1)
                if (book == null) {
                    throw BookNotFoundException("Book not found: ${bibleReference.bookNumber}")
                }
                for (range in bibleReference.ranges) {
                    var chapter = book.Chapter.getOrNull(range.startChapter - 1)
                    if (chapter == null) {
                        throw BookNotFoundException("Chapter not found: ${bibleReference.bookNumber}.${range.startChapter}")
                    }
                    if (range.startChapter == range.endChapter) {
                        Log.d(
                            "BibleRepository",
                            "Loading verses from ${bibleReference.bookNumber}.${range.startChapter} ${range.startVerse}-${range.endVerse}",
                        )
                        verses.addAll(chapter.Verse.subList(range.startVerse - 1, range.endVerse))
                    } else {
                        verses.addAll(
                            chapter.Verse.subList(
                                range.startVerse - 1,
                                chapter.Verse.size,
                            ),
                        )
                        chapter = book.Chapter.getOrNull(range.endChapter - 1)
                        if (chapter == null) {
                            throw BookNotFoundException("Chapter not found: ${bibleReference.bookNumber}.${range.endChapter}")
                        }
                        verses.addAll(chapter.Verse.subList(0, range.endVerse))
                    }
                }
                return verses.map { verse ->
                    val verseId = verse.Verseid.substring(5).toInt() + 1
                    Verse(
                        "$verseId",
                        verse.Verse,
                    )
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("BibleRepository", "Error accessing verses: ${e.message}", e)
            throw BookNotFoundException("Invalid verse range in the request.")
        } catch (e: BookNotFoundException) {
            Log.e("BibleRepository", "Error: ${e.message}", e)
            throw e // Re-throw to be handled by the caller
        }
        return verses
    }

    override fun loadPrefaceTemplates(): PrefaceTemplates = cachedPrefaceTemplates
}