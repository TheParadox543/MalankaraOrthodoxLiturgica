package com.paradox543.malankaraorthodoxliturgica.domain.model

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