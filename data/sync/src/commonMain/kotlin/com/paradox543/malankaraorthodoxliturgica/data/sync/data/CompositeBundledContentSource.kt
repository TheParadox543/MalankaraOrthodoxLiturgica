package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.BundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifestInfo
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import com.paradox543.malankaraorthodoxliturgica.data.sync.Res

class CompositeBundledContentSource(
    private val sources: List<DomainBundledContentSource>,
    private val json: Json
) : BundledContentSource {

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getRootManifest(): RootManifest? {
        return try {
            val bytes = Res.readBytes("files/manifest.json")
            json.decodeFromString(bytes.decodeToString())
        } catch (e: Exception) {
            // Fallback to generating it if physical manifest is missing (unlikely now)
            val domainInfos = mutableMapOf<String, DomainManifestInfo>()
            sources.forEach { source ->
                source.getManifest()?.let { manifest ->
                    domainInfos[source.domain] = DomainManifestInfo(
                        schemaVersion = 1,
                        contentVersion = manifest.contentVersion,
                        manifest = "${source.domain}/manifest.json"
                    )
                }
            }
            if (domainInfos.isEmpty()) return null
            RootManifest(schemaVersion = 1, generatedAt = "", domains = domainInfos)
        }
    }

    override suspend fun getDomainManifest(domain: String): DomainManifest? {
        return sources.find { it.domain == domain }?.getManifest()
    }

    override suspend fun readFile(path: String): String? {
        // Try all sources. Usually path will contain domain prefix or we can infer it.
        // For now, iterate all.
        sources.forEach { source ->
            source.readFile(path)?.let { return it }
        }
        return null
    }
}
