package com.paradox543.malankaraorthodoxliturgica.data.translations.datasource

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.core.datasource.AssetJsonReader
import com.paradox543.malankaraorthodoxliturgica.domain.settings.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import kotlin.collections.iterator

class TranslationSource @Inject constructor(
    private val reader: AssetJsonReader,
) {
    fun loadRawTranslations(): Map<String, Map<String, String>> = reader.loadJsonAsset("translations.json")
}