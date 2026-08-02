package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.BundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.DomainBundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifestInfo
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest

class CompositeBundledContentSource(
    private val sources: List<DomainBundledContentSource>
) : BundledContentSource {

    override suspend fun getRootManifest(): RootManifest? {
        val domainInfos = mutableMapOf<String, DomainManifestInfo>()
        sources.forEach { source ->
            source.getManifest()?.let { manifest ->
                domainInfos[source.domain] = DomainManifestInfo(
                    version = manifest.contentVersion,
                    path = "${source.domain}/manifest.json" // Placeholder path
                )
            }
        }
        if (domainInfos.isEmpty()) return null
        return RootManifest(version = 0, domains = domainInfos)
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
