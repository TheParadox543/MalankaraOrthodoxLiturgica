package com.paradox543.malankaraorthodoxliturgica.domain.repository

import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.AppLanguage

interface TranslationsRepository {
    suspend fun loadTranslations(language: AppLanguage): Map<String, String>
}