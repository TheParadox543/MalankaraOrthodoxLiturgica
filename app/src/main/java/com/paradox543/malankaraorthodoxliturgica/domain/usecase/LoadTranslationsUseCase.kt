package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.settings.repository.TranslationsRepository
import javax.inject.Inject

class LoadTranslationsUseCase @Inject constructor(
    private val translationsRepository: TranslationsRepository,
) {
    suspend operator fun invoke(language: AppLanguage): Map<String, String> = translationsRepository.loadTranslations(language)
}