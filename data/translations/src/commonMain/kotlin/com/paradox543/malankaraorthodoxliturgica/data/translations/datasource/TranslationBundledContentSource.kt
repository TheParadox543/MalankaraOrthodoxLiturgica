package com.paradox543.malankaraorthodoxliturgica.data.translations.datasource

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.paradox543.malankaraorthodoxliturgica.data.translations.Res

class TranslationBundledContentSource(
    private val json: Json
) : DomainBundledContentSource {
    override val domain: String = "translations"

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getManifest(): DomainManifest? {
        return try {
            val bytes = Res.readBytes("files/translations/manifest.json")
            json.decodeFromString(bytes.decodeToString())
        } catch (e: Exception) {
            null
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun readFile(path: String): String? {
        // Translation files might not have a domain prefix in their path if manifest says so
        // But based on ls -R, they are at translations/translations.json
        return try {
            val bytes = Res.readBytes("files/$path")
            bytes.decodeToString()
        } catch (e: Exception) {
            null
        }
    }
}
