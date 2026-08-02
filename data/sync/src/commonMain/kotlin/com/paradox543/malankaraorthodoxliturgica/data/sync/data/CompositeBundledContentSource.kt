package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.data.sync.Res
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.BundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifestInfo
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

class CompositeBundledContentSource(
    private val sources: List<DomainBundledContentSource>,
    private val json: Json,
) : BundledContentSource {
    private val tag = "BundledSource"

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun getRootManifest(): RootManifest? {
        return try {
            val bytes = Res.readBytes("files/manifest.json")
            val manifest = json.decodeFromString<RootManifest>(bytes.decodeToString())
            AppLogger.d(tag) { "Loaded bundled root manifest: schema v${manifest.schemaVersion}" }
            manifest
        } catch (e: Exception) {
            AppLogger.w(tag) { "Failed to load bundled manifest.json: ${e.message}. Generating composite..." }
            // Fallback to generating it if physical manifest is missing (unlikely now)
            val domainInfos = mutableMapOf<String, DomainManifestInfo>()
            sources.forEach { source ->
                source.getManifest()?.let { manifest ->
                    domainInfos[source.domain] =
                        DomainManifestInfo(
                            schemaVersion = 1,
                            contentVersion = manifest.contentVersion,
                            manifest = "${source.domain}/manifest.json",
                        )
                }
            }
            if (domainInfos.isEmpty()) {
                AppLogger.w(tag) { "No bundled domain manifests found." }
                return null
            }
            RootManifest(schemaVersion = 1, generatedAt = "", domains = domainInfos)
        }
    }

    override suspend fun getDomainManifest(domain: String): DomainManifest? {
        val manifest = sources.find { it.domain == domain }?.getManifest()
        if (manifest != null) {
            AppLogger.v(tag) { "Loaded bundled domain manifest for $domain: v${manifest.contentVersion}" }
        }
        return manifest
    }

    override suspend fun readFile(path: String): String? {
        // Try all sources. Usually path will contain domain prefix or we can infer it.
        // For now, iterate all.
        sources.forEach { source ->
            source.readFile(path)?.let {
                AppLogger.v(tag) { "Read file from bundled source: $path" }
                return it
            }
        }
        return null
    }
}
