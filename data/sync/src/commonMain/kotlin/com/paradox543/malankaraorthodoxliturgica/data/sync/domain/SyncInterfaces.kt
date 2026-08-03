package com.paradox543.malankaraorthodoxliturgica.data.sync.domain

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.SyncState
import com.paradox543.malankaraorthodoxliturgica.domain.sync.ContentUpdateSignal
import kotlinx.coroutines.flow.StateFlow

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

    suspend fun writeFile(
        path: String,
        content: String,
    )

    suspend fun exists(path: String): Boolean

    suspend fun getLastSyncTime(): Long

    suspend fun saveLastSyncTime(time: Long)
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

interface Synchronizer : ContentUpdateSignal {
    val syncState: StateFlow<SyncState>

    suspend fun synchronize()
}
