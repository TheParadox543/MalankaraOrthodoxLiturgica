package com.paradox543.malankaraorthodoxliturgica.data.bible.model

class BibleParsingException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)