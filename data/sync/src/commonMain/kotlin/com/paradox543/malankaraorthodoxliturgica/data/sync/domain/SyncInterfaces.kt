package com.paradox543.malankaraorthodoxliturgica.data.sync.domain

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.SyncState
import kotlinx.coroutines.flow.Flow

interface BundledContentSource {
    suspend fun getRootManifest(): RootManifest?
    suspend fun getDomainManifest(domain: String): DomainManifest?
    suspend fun readFile(path: String): String?
}

interface DomainBundledContentSource {
    val domain: String
    suspend fun getManifest(): DomainManifest?
    suspend fun readFile(path: String): String?
}

interface LocalContentStore {
    suspend fun getRootManifest(): RootManifest?
    suspend fun getDomainManifest(domain: String): DomainManifest?
    suspend fun saveRootManifest(manifest: RootManifest)
    suspend fun saveDomainManifest(manifest: DomainManifest)
    suspend fun readFile(path: String): String?
    suspend fun writeFile(path: String, content: String)
    suspend fun exists(path: String): Boolean
}

interface RemoteContentSource {
    suspend fun fetchRootManifest(): RootManifest
    suspend fun fetchDomainManifest(path: String): DomainManifest
    suspend fun downloadFile(path: String): String
}

interface ContentResolver {
    suspend fun resolveFile(path: String): String?
    suspend fun resolveDomainManifest(domain: String): DomainManifest?
}

interface Synchronizer {
    val syncState: Flow<SyncState>
    suspend fun synchronize()
}
