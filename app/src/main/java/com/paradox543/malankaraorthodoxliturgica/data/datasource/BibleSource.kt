package com.paradox543.malankaraorthodoxliturgica.data.datasource

import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleChapterDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceTemplatesData
import javax.inject.Inject

class BibleSource @Inject constructor(
    private val reader: AssetJsonReader,
) {
    fun readBibleDetails(): List<BibleBookDetailsDto>? = reader.loadJsonAsset<List<BibleBookDetailsDto>>("bibleBookMetadata.json")

    fun readPrefaceTemplates(): PrefaceTemplatesData? = reader.loadJsonAsset<PrefaceTemplatesData>("bible_preface_templates.json")

    fun readBibleChapter(path: String): BibleChapterDto? = reader.loadJsonAsset<BibleChapterDto>(path)
}