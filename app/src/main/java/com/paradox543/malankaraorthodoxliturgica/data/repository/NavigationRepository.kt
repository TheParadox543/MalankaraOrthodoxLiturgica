package com.paradox543.malankaraorthodoxliturgica.data.repository

import android.content.Context
import com.paradox543.malankaraorthodoxliturgica.data.model.AppLanguage
import com.paradox543.malankaraorthodoxliturgica.data.model.PageNode
import com.paradox543.malankaraorthodoxliturgica.navigation.NavigationTree.BASE_TREE
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    /**
     * The base navigation tree enriched with available languages for each node.
     */
    val navTree = enrichLanguages(context, BASE_TREE)

    /**
     * Checks the app's assets to determine which languages have the specified file.
     *
     * @param context The application context used to access assets.
     * @param baseFilename The filename to check for (e.g., "index.md").
     * @param extensions The list of AppLanguage enums to check against. Defaults to all available languages.
     * @return A list of language codes (e.g., ["ml", "en"]) that have the specified file in their respective directories.
     */
    private fun getAvailableLanguages(
        context: Context,
        baseFilename: String,
        extensions: List<AppLanguage> = AppLanguage.entries,
    ): List<String> =
        extensions.mapNotNull { lang ->
            try {
                context.assets.open("prayers/${lang.code}/$baseFilename").close()
                lang.code
            } catch (_: IOException) {
                null
            }
        }

    /**
     * Recursively enriches a PageNode tree with the languages available for each node.
     *
     * For file nodes, it checks directly for the file's existence in each language directory.
     * For folder nodes, it aggregates the languages from its children.
     * @param context The application context used to access assets.
     * @param node The current PageNode to enrich.
     * @return A new PageNode instance with the languages property populated.
     */
    fun enrichLanguages(
        context: Context,
        node: PageNode,
    ): PageNode {
        val enrichedChildren = node.children.map { enrichLanguages(context, it) }

        val detectedLanguages =
            if (node.filename != null) {
                // File → detect directly
                getAvailableLanguages(context, node.filename)
            } else {
                // Folder → union of children’s languages
                enrichedChildren.flatMap { it.languages }.distinct()
            }

        return node.copy(
            languages = detectedLanguages,
            children = node.children.map { enrichLanguages(context, it) },
        )
    }

    /**
     * Filters the base navigation tree to include only nodes that support the target language.
     *
     * @param targetLanguage The language code (e.g., "ml" for Malayalam, "en" for English) to filter by.
     * @return A new PageNode tree containing only the nodes (and their valid children)
     * that support the specified language. If the root node itself doesn't
     * support the language, an empty root node (with no children) is returned.
     */
    fun getNavigationTree(targetLanguage: String): PageNode {
        // Start filtering from the navTree's children.
        // We assume the root node itself is a universal container, or its languages list defines
        // if *any* part of the app is available for this language.
        // If the root node must also be language-dependent, you can check it here too.
        // For simplicity, we'll always return a root node, but its children will be filtered.

        // If the root node is meant to be skipped if its own language list doesn't contain the target,
        // you might return null from here and handle it upstream, or return navTree.copy(children = emptyList())
        // if no content is found for the language.
        if (!navTree.languages.contains(targetLanguage)) {
            // If the root node itself isn't available in the target language,
            // return an empty tree (root with no children).
            return navTree.copy(children = emptyList())
        }

        val filteredChildren =
            navTree.children.mapNotNull { childNode ->
                filterNodeByLanguageRecursive(childNode, targetLanguage)
            }
        // Return a new copy of the root node with only the filtered children
        return navTree.copy(children = filteredChildren)
    }

    /**
     * Recursively filters a PageNode and its children based on the target language.
     *
     * @param node The current PageNode to evaluate.
     * @param targetLanguage The language code to filter by.
     * @return A new PageNode instance with only the children that support the language,
     * or null if the current node itself does not support the target language.
     */
    private fun filterNodeByLanguageRecursive(
        node: PageNode,
        targetLanguage: String,
    ): PageNode? {
        // Step 1: Check if the current node supports the target language
        if (!node.languages.contains(targetLanguage)) {
            return null // If not, this node and its entire subtree are excluded
        }

        // Step 2: Recursively filter the children of the current node
        val filteredChildren =
            node.children.mapNotNull { childNode ->
                filterNodeByLanguageRecursive(childNode, targetLanguage)
            }

        // Step 3: If the current node supports the language,
        // return a NEW PageNode instance with its original properties
        // but with the newly filtered list of children.
        return node.copy(children = filteredChildren)
    }
}