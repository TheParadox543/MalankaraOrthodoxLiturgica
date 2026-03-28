package com.paradox543.malankaraorthodoxliturgica.domain.translations.repository

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

suspend fun TranslationsRepository.loadTranslations(
    language: AppLanguage,
    dispatcher: CoroutineDispatcher,
): Map<String, String> =
    withContext(dispatcher) {
        loadTranslations(language)
    }

