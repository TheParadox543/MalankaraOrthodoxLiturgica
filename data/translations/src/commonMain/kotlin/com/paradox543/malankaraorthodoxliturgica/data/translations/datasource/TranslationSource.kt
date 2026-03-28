package com.paradox543.malankaraorthodoxliturgica.data.translations.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader

interface RawTranslationsSource {
    fun loadRawTranslations(): Map<String, Map<String, String>>
}

class TranslationSource(
    private val reader: AssetJsonReader,
) : RawTranslationsSource {
    override fun loadRawTranslations(): Map<String, Map<String, String>> =
        reader.loadJsonAsset("translations.json")
}