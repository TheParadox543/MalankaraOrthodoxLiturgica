package com.paradox543.malankaraorthodoxliturgica.data.prayer.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.prayer.Res as prayerRes

class PrayerComposeResourceReader : ResourceTextReader {
    override suspend fun readText(path: String): String =
        prayerRes
            .readBytes("files/$path")
            .decodeToString()
}