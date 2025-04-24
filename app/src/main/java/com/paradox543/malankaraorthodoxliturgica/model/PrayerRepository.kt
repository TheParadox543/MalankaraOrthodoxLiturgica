package com.paradox543.malankaraorthodoxliturgica.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _selectedLanguage = MutableStateFlow("ml") // Default language
    val selectedLanguage: StateFlow<String> = _selectedLanguage

    private val _translations = MutableStateFlow<Map<String, Map<String, String>>>(emptyMap())
    val translations: StateFlow<Map<String, Map<String, String>>> = _translations

    fun loadTranslations(language: String): Map<String, String>{
        val json = context.assets.open("translations.json").bufferedReader().use {it.readText()}
        val jsonObject = JSONObject(json)
        val translationMap = mutableMapOf<String, String>()
        for (key in jsonObject.keys()) {
            val innerObject = jsonObject.getJSONObject(key)
            translationMap[key] = innerObject.getString(language)
        }
        return translationMap
    }

    private val _prayers = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val prayers: StateFlow<List<Map<String, String>>> = _prayers

    fun loadPrayers(filename: String, language: String): List<Map<String, String>> {
        val json = context.assets.open("prayers/$filename").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        val prayerList = mutableListOf<Map<String, String>>()

        for (i in 0 until jsonArray.length()) {
            val prayerObject = jsonArray.getJSONObject(i)
            val type = prayerObject.getString("type")
            if (type == "link") {
                val linkedFile = prayerObject.getString("file")
                prayerList.addAll(loadPrayers(linkedFile, language)) // Recursively load linked file
            } else {
                var content = prayerObject.getString("lit_${language}")
                if (content.isEmpty()) {
                    content = prayerObject.getString("lit_ml")
                }
                prayerList.add(mapOf("type" to type, "content" to content))
            }
        }
        return prayerList
    }
}
