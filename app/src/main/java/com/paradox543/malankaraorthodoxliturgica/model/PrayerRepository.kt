package com.paradox543.malankaraorthodoxliturgica.model

import org.json.JSONObject
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray

class PrayerRepository(private val context: Context) {
    fun getCategories(): List<String> = listOf("daily_prayers", "sacrament")

    fun getCategoryPrayers(category: String): List<String> {
        return when (category) {
            "daily_prayers" -> listOf("sleeba", "kyamtha", "nineveh", "great_lent")
            "sacrament" -> listOf("qurbana", "baptism", "wedding", "funeral")
//            "Feast Day Prayers" -> listOf("Christmas", "Easter", "Ascension")
            else -> emptyList()
        }
    }

    fun getGreatLentDays(): List<String> = listOf("monday", "tuesday", "wednesday", "thursday", "friday")

    fun getDayPrayers(): List<String> = listOf("sandhya", "soothara", "rathri", "prabatham", "3rd", "6th", "9th")

    fun getQurbanaSections(): List<String> = listOf(
        "Preparation",
        "Part One",
        "Part Two Chapter One",
        "Part Two Chapter Two",
        "Part Two Chapter Three",
        "Part Two Chapter Four",
        "Part Two Chapter Five"
    )

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
        val json = context.assets.open(filename).bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        val prayerList = mutableListOf<Map<String, String>>()

        for (i in 0 until jsonArray.length()) {
            val prayerObject = jsonArray.getJSONObject(i)
            val type = prayerObject.getString("type")
            var content = prayerObject.getString("lit_${language}")
            if (content.isEmpty()){
                content = prayerObject.getString("lit_ml")
            }

            prayerList.add(mapOf("type" to type, "content" to content))
        }

        return prayerList
    }
}
