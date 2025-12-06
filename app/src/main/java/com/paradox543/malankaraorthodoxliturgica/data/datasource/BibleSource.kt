package com.paradox543.malankaraorthodoxliturgica.data.datasource

import com.paradox543.malankaraorthodoxliturgica.data.model.BibleBookDetailsData
import com.paradox543.malankaraorthodoxliturgica.data.model.BibleChapterData
import com.paradox543.malankaraorthodoxliturgica.data.model.PrefaceTemplatesData
import javax.inject.Inject

class BibleSource @Inject constructor(
    private val reader: AssetJsonReader,
) {
    fun readBibleDetails(): List<BibleBookDetailsData>? = reader.loadJsonAsset<List<BibleBookDetailsData>>("bibleBookMetadata.json")

    fun readPrefaceTemplates(): PrefaceTemplatesData? = reader.loadJsonAsset<PrefaceTemplatesData>("bible_preface_templates.json")

    fun readBibleChapter(path: String): BibleChapterData? = reader.loadJsonAsset<BibleChapterData>(path)
}