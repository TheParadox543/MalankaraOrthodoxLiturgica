package com.paradox543.malankaraorthodoxliturgica.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    val navigationTree: PageNode by lazy {
        loadNavigationFromAssets()
    }

    fun loadNavigationFromAssets(): PageNode {
        val jsonString = context.assets.open("navigation.json")
            .bufferedReader().use { it.readText() }

        return json.decodeFromString(jsonString)
    }

    private fun findNodeByRoute(route: String, node: PageNode = navigationTree): PageNode? {
        if (node.route == route) return node
        node.children?.forEach { child ->
            val result = findNodeByRoute(route, child)
            if (result != null) return result
        }
        return null
    }
}