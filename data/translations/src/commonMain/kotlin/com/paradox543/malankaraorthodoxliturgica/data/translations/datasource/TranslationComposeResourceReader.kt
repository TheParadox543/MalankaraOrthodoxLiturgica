package com.paradox543.malankaraorthodoxliturgica.data.translations.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.translations.Res as translationsRes

class TranslationComposeResourceReader : ResourceTextReader {
    override suspend fun readText(path: String): String =
        translationsRes
            .readBytes("files/$path")
            .decodeToString()
}