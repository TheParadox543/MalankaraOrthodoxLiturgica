package com.paradox543.malankaraorthodoxliturgica.utils

fun String.applyPrayerReplacements(): String =
    this
        .replace("/t", "    ")
        .replace("/u200b", "\u200b")