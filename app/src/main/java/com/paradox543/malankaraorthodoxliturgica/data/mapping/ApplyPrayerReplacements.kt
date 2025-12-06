package com.paradox543.malankaraorthodoxliturgica.data.mapping

fun String.applyPrayerReplacements(): String =
    this
        .replace("/t", "    ")
        .replace("/u200b", "\u200b")