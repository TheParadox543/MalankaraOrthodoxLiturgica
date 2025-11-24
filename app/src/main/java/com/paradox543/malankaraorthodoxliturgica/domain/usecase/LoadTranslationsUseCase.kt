package com.paradox543.malankaraorthodoxliturgica.domain.usecase

import com.paradox543.malankaraorthodoxliturgica.domain.repository.TranslationsRepository
import com.paradox543.malankaraorthodoxliturgica.shared.domain.model.AppLanguage
import javax.inject.Inject

class LoadTranslationsUseCase @Inject constructor(
    private val translationsRepository: TranslationsRepository,
) {
    suspend operator fun invoke(language: AppLanguage): Map<String, String> = translationsRepository.loadTranslations(language)
}