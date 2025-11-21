package com.paradox543.malankaraorthodoxliturgica.data.repository

import com.paradox543.malankaraorthodoxliturgica.data.datasource.TranslationSource
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.repository.TranslationsRepository
import javax.inject.Inject

class TranslationsRepositoryImpl @Inject constructor(
    private val translationsSource: TranslationSource,
) : TranslationsRepository {
    override suspend fun loadTranslations(language: AppLanguage): Map<String, String> {
        return translationsSource.loadTranslations(language)
    }
}