package com.paradox543.malankaraorthodoxliturgica.data.model

enum class AppLanguage(val code: String) {
    MALAYALAM("ml"),
    ENGLISH("en"),
    MANGLISH("mn");

    companion object {
        fun fromCode(code: String): AppLanguage? {
            return entries.find { it.code == code }
        }
    }
}