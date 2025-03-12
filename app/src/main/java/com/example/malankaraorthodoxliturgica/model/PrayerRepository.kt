package com.example.malankaraorthodoxliturgica.model

import androidx.compose.runtime.Composable
import org.json.JSONObject
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.malankaraorthodoxliturgica.viewmodel.PrayerViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray

class PrayerRepository(private val context: Context) {
    fun getCategories(): List<String> = listOf("Daily Prayers", "Sacramental Prayers")

    fun getCategoryPrayers(category: String): List<String> {
        return when (category) {
            "Daily Prayers" -> listOf("Sleeba", "Kyamtha", "Nineveh Lent", "Great Lent")
            "Sacramental Prayers" -> listOf("Qurbana", "Baptism", "Wedding", "Funeral")
//            "Feast Day Prayers" -> listOf("Christmas", "Easter", "Ascension")
            else -> emptyList()
        }
    }

    fun getGreatLentDays(): List<String> = listOf("monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    fun getDayPrayers(): List<String> = listOf("Sandhya", "Soothara", "Rathri", "Prabatham", "3rd Hour", "6th Hour", "9th Hour")

    fun getQurbanaSections(): List<String> = listOf(
        "Preparation",
        "Part One",
        "Part Two Chapter One",
        "Part Two Chapter Two",
        "Part Two Chapter Three",
        "Part Two Chapter Four",
        "Part Two Chapter Five"
    )

    private val _prayers = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val prayers: StateFlow<List<Map<String, String>>> = _prayers
    fun loadPrayers(filename: String): List<Map<String, String>> {
        val json = context.assets.open(filename).bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(json)
        val prayerList = mutableListOf<Map<String, String>>()

        for (i in 0 until jsonArray.length()) {
            val prayerObject = jsonArray.getJSONObject(i)
            val type = prayerObject.getString("type")
//            val content = if (type == "song") {
//                prayerObject.getJSONArray("lit_ml").let { jsonArrayObj ->
//                    List(jsonArrayObj.length()) { index -> jsonArrayObj.getString(index) }
//                }.joinToString("\n\n") // Separate stanzas
//            } else {
//                prayerObject.getString("lit_ml")
//            }
            val content = prayerObject.getString("lit_ml")

            prayerList.add(mapOf("type" to type, "content" to content))
        }

        return prayerList
    }
}
