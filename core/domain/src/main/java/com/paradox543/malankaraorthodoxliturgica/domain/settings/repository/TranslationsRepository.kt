package com.paradox543.malankaraorthodoxliturgica.domain.settings.repository

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage

interface TranslationsRepository {
    suspend fun loadTranslations(language: AppLanguage): Map<String, String>
}