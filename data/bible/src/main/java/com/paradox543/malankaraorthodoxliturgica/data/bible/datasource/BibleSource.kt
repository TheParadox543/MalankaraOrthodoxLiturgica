package com.paradox543.malankaraorthodoxliturgica.data.bible.datasource

import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleBookDetailsDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.BibleChapterDto
import com.paradox543.malankaraorthodoxliturgica.data.bible.model.PrefaceTemplatesDto
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import javax.inject.Inject

class BibleSource @Inject constructor(
    private val reader: AssetJsonReader,
) {
    fun readBibleDetails(): List<BibleBookDetailsDto> = reader.loadJsonAsset("bibleBookMetadata.json")

    fun readPrefaceTemplates(): PrefaceTemplatesDto = reader.loadJsonAsset("bible_preface_templates.json")

    fun readBibleChapter(path: String): BibleChapterDto = reader.loadJsonAsset(path)
}