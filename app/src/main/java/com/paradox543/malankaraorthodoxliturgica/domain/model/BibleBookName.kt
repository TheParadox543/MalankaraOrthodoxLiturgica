package com.paradox543.malankaraorthodoxliturgica.domain.model

/**
 * Localized display names for a Bible book.
 *
 * Stores the English and Malayalam forms of a book name and provides a helper
 * to select the appropriate string for the current [AppLanguage].
 *
 * @property en English name.
 * @property ml Malayalam name.
 */
data class BibleBookName(
    val en: String,
    val ml: String,
) {
    fun get(language: AppLanguage): String =
        when (language) {
            AppLanguage.MALAYALAM -> ml
            else -> en
        }
}