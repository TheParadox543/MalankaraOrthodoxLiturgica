package com.paradox543.malankaraorthodoxliturgica.core.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

private enum class PrayerTextStyle {
    NORMAL,
    BOLD,
    ITALIC,
}

private val tokenRegex = Regex("""\S+|\s+""")

private val TAG_PREFIXES = listOf("/b:", "/i:")

fun String.toPrayerAnnotatedString(): AnnotatedString {
    if (TAG_PREFIXES.none(::contains)) {
        return AnnotatedString(this)
    }

    val builder = AnnotatedString.Builder()
    var currentStyle = PrayerTextStyle.NORMAL

    tokenRegex.findAll(this).forEach { match ->
        val token = match.value

        // Whitespace is appended verbatim.
        if (token.isBlank()) {
            builder.append(token)
            return@forEach
        }

        var word = token

        // Opening tags
        when {
            word.startsWith("/b:") -> {
                currentStyle = PrayerTextStyle.BOLD
                word = word.removePrefix("/b:")
            }

            word.startsWith("/i:") -> {
                currentStyle = PrayerTextStyle.ITALIC
                word = word.removePrefix("/i:")
            }
        }

        // Closing tags
        var resetStyle = false

        if (word.endsWith("/b0")) {
            word = word.removeSuffix("/b0")
            resetStyle = true
        } else if (word.endsWith("/i0")) {
            word = word.removeSuffix("/i0")
            resetStyle = true
        }

        val spanStyle =
            when (currentStyle) {
                PrayerTextStyle.NORMAL -> {
                    SpanStyle()
                }

                PrayerTextStyle.BOLD -> {
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                    )
                }

                PrayerTextStyle.ITALIC -> {
                    SpanStyle(
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

        builder.withStyle(spanStyle) {
            append(word)
        }

        if (resetStyle) {
            currentStyle = PrayerTextStyle.NORMAL
        }
    }

    return builder.toAnnotatedString()
}