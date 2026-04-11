package com.paradox543.malankaraorthodoxliturgica.data.bible.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.bible.Res as bibleRes

class BibleComposeResourceReader : ResourceTextReader {
    override suspend fun readText(path: String): String = bibleRes.readBytes("files/$path").decodeToString()
}