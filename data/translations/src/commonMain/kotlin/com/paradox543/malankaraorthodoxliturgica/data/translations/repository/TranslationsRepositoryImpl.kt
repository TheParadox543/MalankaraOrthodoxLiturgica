package com.paradox543.malankaraorthodoxliturgica.data.translations.repository

import com.paradox543.malankaraorthodoxliturgica.data.translations.datasource.RawTranslationsSource
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.domain.translations.repository.TranslationsRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TranslationsRepositoryImpl(
    private val source: RawTranslationsSource,
) : TranslationsRepository {
    private val cacheMutex = Mutex()
    private var cachedRaw: Map<String, Map<String, String>>? = null
    private val languageCache = mutableMapOf<String, Map<String, String>>()

    override suspend fun loadTranslations(language: AppLanguage): Map<String, String> {
        val code = language.cacheLanguageCode()
        languageCache[code]?.let { return it }

        return cacheMutex.withLock {
            languageCache[code]?.let { return@withLock it }

            val raw =
                cachedRaw ?: source.loadRawTranslations().also {
                    cachedRaw = it
                }

            val loaded =
                raw.mapValues { (_, translations) ->
                    translations[code] ?: ""
                }
            languageCache[code] = loaded
            loaded
        }
    }

    private fun AppLanguage.cacheLanguageCode(): String =
        when (this) {
            AppLanguage.MALAYALAM -> AppLanguage.MALAYALAM.code
            AppLanguage.ENGLISH,
            AppLanguage.MANGLISH,
            AppLanguage.INDIC,
            -> AppLanguage.ENGLISH.code
        }
}