package com.paradox543.malankaraorthodoxliturgica.domain.model

import okio.IOException

class BookNotFoundException(
    message: String,
) : IOException(message)

class PrayerContentNotFoundException(
    message: String,
) : IOException(message)

class PrayerParsingException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

class PrayerLinkDepthExceededException(
    message: String,
) : Exception(message)

class BibleParsingException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)