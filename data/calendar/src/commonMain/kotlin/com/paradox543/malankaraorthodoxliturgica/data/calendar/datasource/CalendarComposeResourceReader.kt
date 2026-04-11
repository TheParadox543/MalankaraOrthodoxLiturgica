package com.paradox543.malankaraorthodoxliturgica.data.calendar.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.ResourceTextReader
import com.paradox543.malankaraorthodoxliturgica.data.calendar.Res as calendarRes

class CalendarComposeResourceReader : ResourceTextReader {
    override suspend fun readText(path: String): String =
        calendarRes
            .readBytes("files/$path")
            .decodeToString()
}

