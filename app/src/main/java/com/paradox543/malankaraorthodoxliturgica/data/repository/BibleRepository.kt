package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleBook
import com.paradox543.malankaraorthodoxliturgica.data.model.BookName
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun loadBibleChapters(): List<BibleBook> {
        val json = context.assets.open("bibleBooks.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        val bibleChapters = mutableListOf<BibleBook>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val bookObject = jsonObject.getJSONObject("book")
            val englishName = bookObject.getString("en")
            val malayalamName = bookObject.getString("ml")
            val book = BookName(englishName, malayalamName)
            val verses = jsonObject.getInt("verses")
            val chapters = jsonObject.getInt("chapters")
            bibleChapters.add(BibleBook(book, chapters, verses))
        }
        return bibleChapters
    }

    fun loadBibleChapter(bookIndex: Int, chapterIndex: Int, language: String = "ml"): Map<String, String> {
        val json = context.assets.open("bible-$language.json").bufferedReader().use { it.readText() }
        val bibleObject = JSONObject(json)
        val bookObject = bibleObject.getJSONArray("Book")[bookIndex] as JSONObject
        val chapterObject = bookObject.getJSONArray("Chapter")[chapterIndex] as JSONObject
        val versesArray = chapterObject.getJSONArray("Verse")
        val versesMap = mutableMapOf<String, String>()
        for(i in 0 until versesArray.length()) {
            val verseObject = versesArray.getJSONObject(i)
            val verseNumber = (i + 1).toString()
            val verseText = verseObject.getString("Verse")
            versesMap[verseNumber] = verseText
        }
        return versesMap
    }
}