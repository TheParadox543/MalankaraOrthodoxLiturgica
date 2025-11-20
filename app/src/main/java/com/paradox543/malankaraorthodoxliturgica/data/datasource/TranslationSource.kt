package com.paradox543.malankaraorthodoxliturgica.data.datasource

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.domain.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import kotlin.collections.iterator

class TranslationSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun loadTranslations(language: AppLanguage): Map<String, String> {
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
        return translationMap
    }
}