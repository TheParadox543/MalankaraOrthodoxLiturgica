package com.paradox543.malankaraorthodoxliturgica.data.translations.repository

import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.TranslationSource
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationsRepositoryImpl @Inject constructor(
    private val source: TranslationSource,
) : TranslationsRepository {
    private var cachedRaw: Map<String, Map<String, String>>? = null
    private val languageCache = mutableMapOf<AppLanguage, Map<String, String>>()

    override suspend fun loadTranslations(language: AppLanguage): Map<String, String> =
        languageCache.getOrPut(language) {
            val raw =
                cachedRaw ?: source.loadRawTranslations().also {
                    cachedRaw = it
                }

            val code =
                when (language) {
                    AppLanguage.MALAYALAM -> AppLanguage.MALAYALAM.code

                    AppLanguage.ENGLISH,
                    AppLanguage.MANGLISH,
                    AppLanguage.INDIC,
                    -> AppLanguage.ENGLISH.code
                }

            raw.mapValues { (_, translations) ->
                translations[code] ?: ""
            }
        }
}