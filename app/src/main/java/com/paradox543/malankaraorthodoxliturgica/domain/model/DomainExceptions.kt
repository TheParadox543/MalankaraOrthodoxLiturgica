package com.paradox543.malankaraorthodoxliturgica.domain.model

import okio.IOException

class PrayerContentNotFoundException(
    message: String,
) : IOException(message)

class PrayerParsingException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

class BibleParsingException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)