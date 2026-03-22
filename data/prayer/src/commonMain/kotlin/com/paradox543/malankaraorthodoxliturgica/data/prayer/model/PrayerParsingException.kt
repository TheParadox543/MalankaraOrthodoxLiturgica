package com.paradox543.malankaraorthodoxliturgica.data.prayer.model

class PrayerParsingException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)