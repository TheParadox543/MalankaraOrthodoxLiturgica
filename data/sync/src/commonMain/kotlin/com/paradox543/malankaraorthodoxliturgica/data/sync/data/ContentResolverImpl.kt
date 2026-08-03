package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.BundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.ContentResolver
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.LocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest

class ContentResolverImpl(
    private val localStore: LocalContentStore,
    private val bundledSource: BundledContentSource
) : ContentResolver {

    override suspend fun resolveFile(path: String): String? {
        return localStore.readFile(path) ?: bundledSource.readFile(path)
    }

    override suspend fun resolveDomainManifest(domain: String): DomainManifest? {
        return localStore.getDomainManifest(domain) ?: bundledSource.getDomainManifest(domain)
    }
}
