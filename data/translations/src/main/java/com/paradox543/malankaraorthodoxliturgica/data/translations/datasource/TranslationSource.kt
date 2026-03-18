package com.paradox543.malankaraorthodoxliturgica.data.translations.datasource

import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader

class TranslationSource(
    private val reader: AssetJsonReader,
) {
    fun loadRawTranslations(): Map<String, Map<String, String>> = reader.loadJsonAsset("translations.json")
}