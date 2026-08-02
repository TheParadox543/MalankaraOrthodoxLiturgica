package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.BundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.LocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.RemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.Synchronizer
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.SyncState
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SynchronizerImpl(
    private val localStore: LocalContentStore,
    private val bundledSource: BundledContentSource,
    private val remoteSource: RemoteContentSource,
) : Synchronizer {
    private val _syncState = MutableStateFlow(SyncState(SyncStatus.IDLE))
    override val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    override suspend fun synchronize() {
        _syncState.value = SyncState(SyncStatus.SYNCING, 0f)
        try {
            val remoteRoot = remoteSource.fetchRootManifest()
            syncDomains(remoteRoot)
            localStore.saveRootManifest(remoteRoot)

            _syncState.value = SyncState(SyncStatus.SUCCESS, 1f)
        } catch (e: Exception) {
            _syncState.value = SyncState(SyncStatus.FAILURE, error = e)
        }
    }

    private suspend fun syncDomains(root: RootManifest) {
        root.domains.forEach { (domain, info) ->
            val localDomainManifest = localStore.getDomainManifest(domain) ?: bundledSource.getDomainManifest(domain)

            if (localDomainManifest == null || info.contentVersion > localDomainManifest.contentVersion) {
                val remoteDomainManifest = remoteSource.fetchDomainManifest(info.manifest)
                syncFiles(remoteDomainManifest, localDomainManifest)
                localStore.saveDomainManifest(remoteDomainManifest)
            }
        }
    }

    private suspend fun syncFiles(
        remoteManifest: DomainManifest,
        localManifest: DomainManifest?,
    ) {
        val localFiles = localManifest?.files?.associateBy { it.location } ?: emptyMap()

        remoteManifest.files.forEach { remoteFile ->
            val localFile = localFiles[remoteFile.location]
            if (localFile == null || localFile.checksum != remoteFile.checksum || !localStore.exists(remoteFile.location)) {
                val content = remoteSource.downloadFile(remoteFile.location)
                localStore.writeFile(remoteFile.location, content)
            }
        }
    }
}
