package com.paradox543.malankaraorthodoxliturgica.data.datasource

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import kotlin.collections.iterator

class TranslationSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    suspend fun loadTranslations(language: AppLanguage): Map<String, String> =
        withContext(Dispatchers.IO) {
            val json =
                context
                    .assets
                    .open("translations.json")
                    .bufferedReader()
                    .use { it.readText() }
            val jsonObject = JSONObject(json)
            val translationMap = mutableMapOf<String, String>()
            for (key in jsonObject.keys()) {
                val innerObject = jsonObject.getJSONObject(key)
                val code =
                    when (language) {
                        AppLanguage.MALAYALAM -> AppLanguage.MALAYALAM.code
                        AppLanguage.ENGLISH, AppLanguage.MANGLISH, AppLanguage.INDIC -> AppLanguage.ENGLISH.code
                    }
                translationMap[key] = innerObject.getString(code)
            }
            return@withContext translationMap
        }
}