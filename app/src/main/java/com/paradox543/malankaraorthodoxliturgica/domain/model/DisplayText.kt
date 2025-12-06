package com.paradox543.malankaraorthodoxliturgica.domain.model

data class DisplayText(
    val en: String,
    val ml: String? = null,
) {
    fun get(language: AppLanguage): String =
        when (language) {
            AppLanguage.MALAYALAM -> ml ?: en
            else -> en
        }
}