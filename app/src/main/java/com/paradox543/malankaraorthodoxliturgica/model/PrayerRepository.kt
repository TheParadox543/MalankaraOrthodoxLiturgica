package com.paradox543.malankaraorthodoxliturgica.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
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

    fun loadPrayers(filename: String, language: String, depth: Int = 0, maxDepth: Int = 5): List<Map<String, Any>> {
        if (depth > maxDepth) {
            return listOf(mapOf("type" to "error", "content" to "Error: Exceeded maximum link depth."))
        }
        val prayerList = mutableListOf<Map<String, Any>>()

        try {
            val json =
                context.assets.open("prayers/$filename").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val prayerObject = jsonArray.getJSONObject(i)
                when (val type = prayerObject.getString("type")) {
                    "link" -> {
                        val linkedFile = prayerObject.getString("file")
                        prayerList.addAll(
                            loadPrayers(
                                linkedFile,
                                language,
                                depth + 1,
                                maxDepth
                            )
                        ) // Recursively load linked file
                    }

                    "link-collapsible" -> {
                        val linkedFile = prayerObject.getString("file")
                        prayerList.add(
                            loadPrayerAsCollapsible(
                                linkedFile,
                                language
                            )
                        ) // Add file as a collapsible block
                    }

                    else -> {
                        var content = prayerObject.optString("lit_${language}")
                        if (content.isEmpty()) {
                            return listOf(mapOf(
                                "type" to "error",
                                "content" to "Content in this language has not been added yet."
                            ))
                        }
                        prayerList.add(mapOf("type" to type, "content" to content))
                    }
                }
            }
        } catch (e: IOException) {
            return listOf(mapOf("type" to "error", "content" to "Error loading file: $filename"))
        } catch (e: org.json.JSONException) {
            return listOf(mapOf("type" to "error", "content" to "Error parsing JSON in: $filename"))
        }
        return prayerList
    }

    private fun loadPrayerAsCollapsible(filename: String, language: String): Map<String, Any>{
        val itemList = mutableListOf<Map<String, String>>()
        var title = ""
        try {
            val json = context.assets.open("prayers/$filename").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val prayerObject = jsonArray.getJSONObject(i)
                val type = prayerObject.getString("type")
                var content = prayerObject.getString("lit_${language}")
                if (content.isEmpty()) {
                    content = prayerObject.getString("lit_ml")
                }
                if (type == "heading" && title.isEmpty()) {
                    title = content
                }
                if (type != "heading") {
                    itemList.add(mapOf("type" to type, "content" to content))
                }
            }
            return mapOf("type" to "collapsible-block", "title" to title, "items" to itemList)
        } catch (e: IOException) {
            return mapOf("type" to "error", "content" to "Error loading file: $filename")
        } catch (e: org.json.JSONException) {
            return mapOf("type" to "error", "content" to "Error parsing JSON in: $filename")
        }
    }
}
