package com.paradox543.malankaraorthodoxliturgica.model

import android.content.Context
import android.util.Log
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
    fun loadTranslations(language: String): Map<String, String> {
        val json = context.assets.open("translations.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)
        val translationMap = mutableMapOf<String, String>()
        for (key in jsonObject.keys()) {
            val innerObject = jsonObject.getJSONObject(key)
            translationMap[key] = innerObject.getString(language)
        }
        return translationMap
    }

    fun loadPrayers(
        filename: String,
        language: String,
        depth: Int = 0,
        maxDepth: Int = 5
    ): List<Map<String, Any>> {
        if (depth > maxDepth) {
            return listOf(
                mapOf(
                    "type" to "error",
                    "content" to "Error: Exceeded maximum link depth."
                )
            )
        }
        val prayerList = mutableListOf<Map<String, Any>>()

        try {
            val json =
                context.assets.open("prayers/$language/$filename").bufferedReader()
                    .use { it.readText() }
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

                    "collapsible" -> {
                        val title = prayerObject.getString("title")
                        val itemList = mutableListOf<Map<String, String>>()
                        val itemsJson = prayerObject.getJSONArray("items")
                        for (j in 0 until itemsJson.length()) {
                            val itemObject = itemsJson.getJSONObject(j)
                            val itemType = itemObject.getString("type")
                            val itemContent = itemObject.optString("content")
                            if (itemContent.isEmpty()) {
                                return listOf(
                                    mapOf(
                                        "type" to "error",
                                        "content" to "Content in this language has not been added yet."
                                    )
                                )
                            }
                            itemList.add(mapOf("type" to itemType, "content" to itemContent))
                        }
                        prayerList.add(
                            mapOf(
                                "type" to "collapsible-block",
                                "title" to title,
                                "items" to itemList
                            )
                        )
                    } // Add content as collapsible block

                    else -> {
                        val content = prayerObject.optString("content")
                        if (content.isEmpty()) {
                            return listOf(
                                mapOf(
                                    "type" to "error",
                                    "content" to "Content in this language has not been added yet."
                                )
                            )
                        }
                        prayerList.add(mapOf("type" to type, "content" to content))
                    }
                }
            }
        } catch (e: IOException) {
            return listOf(
                mapOf(
                    "type" to "error",
                    "content" to "Error loading file: $language/$filename"
                )
            )
        } catch (e: org.json.JSONException) {
            return listOf(
                mapOf(
                    "type" to "error",
                    "content" to "Error parsing JSON in: $language/$filename"
                )
            )
        }
        return prayerList
    }

    private fun loadPrayerAsCollapsible(filename: String, language: String): Map<String, Any> {
        val itemList = mutableListOf<Map<String, String>>()
        var title = ""
        try {
            val json = context.assets.open("prayers/$language/$filename").bufferedReader()
                .use { it.readText() }
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val prayerObject = jsonArray.getJSONObject(i)
                val type = prayerObject.getString("type")
                val content = prayerObject.optString("content")
                if (content.isEmpty()) {
                    return mapOf(
                        "type" to "error",
                        "content" to "Content in this language has not been added yet."
                    )
                }
                if (type == "title") {
                    title = content
                    continue
                } else if (type == "heading" && title.isEmpty()) {
                    title = content
                    continue
                }
                itemList.add(mapOf("type" to type, "content" to content))
            }
            return mapOf("type" to "collapsible-block", "title" to title, "items" to itemList)
        } catch (e: IOException) {
            return mapOf("type" to "error", "content" to "Error loading file: $language/$filename")
        } catch (e: org.json.JSONException) {
            return mapOf(
                "type" to "error",
                "content" to "Error parsing JSON in: $language/$filename"
            )
        }
    }
}