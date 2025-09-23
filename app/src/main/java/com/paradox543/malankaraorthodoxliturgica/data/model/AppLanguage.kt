package com.paradox543.malankaraorthodoxliturgica.data.model

enum class AppLanguage(val code: String, val displayName: String) {
    MALAYALAM("ml", "മലയാളം"),
    ENGLISH("en", "English"),
    MANGLISH("mn", "Manglish (Malayalam in English Script)"),
    INDIC("indic", "Indic (Malayalam in Indic Script)");

    companion object {
        fun fromCode(code: String): AppLanguage? {
            return entries.find { it.code == code }
        }
    }
}