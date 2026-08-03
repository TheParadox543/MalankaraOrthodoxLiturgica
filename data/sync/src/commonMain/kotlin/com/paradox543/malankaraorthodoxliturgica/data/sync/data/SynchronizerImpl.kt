package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.paradox543.malankaraorthodoxliturgica.core.platform.currentTimeMillis
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.BundledContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.LocalContentStore
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.RemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.Synchronizer
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.SyncState
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.SyncStatus
import com.paradox543.malankaraorthodoxliturgica.info.AppInfoProvider
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit

class SynchronizerImpl(
    private val localStore: LocalContentStore,
    private val bundledSource: BundledContentSource,
    private val remoteSource: RemoteContentSource,
    private val appInfoProvider: AppInfoProvider,
) : Synchronizer {
    private val tag = "Synchronizer"
    private val _syncState = MutableStateFlow(SyncState(SyncStatus.IDLE))
    override val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _onDomainUpdated = MutableSharedFlow<String>()
    override val onDomainUpdated: SharedFlow<String> = _onDomainUpdated.asSharedFlow()

    private val _onAnyUpdate = MutableSharedFlow<Unit>()
    override val onAnyUpdate: SharedFlow<Unit> = _onAnyUpdate.asSharedFlow()

    private val throttleInterval: Long = 24 * 60 * 60 * 1000L // 24 hours in ms

    override suspend fun synchronize() =
        coroutineScope {
            val now: Long = currentTimeMillis()
            val lastSync: Long = localStore.getLastSyncTime()
            val diff: Long = now - lastSync
            val isThrottled: Boolean = if (appInfoProvider.debugMode) false else diff < throttleInterval

            _syncState.value = SyncState(SyncStatus.SYNCING, 0f)
            AppLogger.d(tag) { "Starting synchronization (throttled: $isThrottled)..." }

            try {
                val remoteRoot = remoteSource.fetchRootManifest()
                val localRoot = localStore.getRootManifest() ?: bundledSource.getRootManifest()

                // If throttled and versions match, skip full sync
                if (isThrottled && localRoot != null && remoteRoot.schemaVersion <= localRoot.schemaVersion) {
                    val allDomainsUpToDate =
                        remoteRoot.domains.all { (domain, info) ->
                            val localDomain =
                                localStore.getDomainManifest(domain) ?: bundledSource.getDomainManifest(domain)
                            localDomain != null && info.contentVersion <= localDomain.contentVersion
                        }
                    if (allDomainsUpToDate) {
                        AppLogger.i(tag) { "Sync skipped (24h throttle and version matches)." }
                        _syncState.value = SyncState(SyncStatus.SUCCESS, 1f, hasUpdate = false)
                        return@coroutineScope
                    }
                }

                AppLogger.d(
                    tag,
                ) { "Remote Root Manifest fetched: schemaVersion=${remoteRoot.schemaVersion}" }

                val anyUpdated = syncDomains(remoteRoot)
                localStore.saveRootManifest(remoteRoot)
                localStore.saveLastSyncTime(now)

                if (anyUpdated) {
                    _onAnyUpdate.emit(Unit)
                }

                AppLogger.i(tag) { "Synchronization completed successfully. Updated: $anyUpdated" }
                _syncState.value = SyncState(SyncStatus.SUCCESS, 1f, hasUpdate = anyUpdated)
            } catch (e: CancellationException) {
                AppLogger.w(tag) { "Synchronization cancelled (timed out)." }
                throw e
            } catch (e: Exception) {
                AppLogger.e(tag, e) { "Synchronization failed: ${e.message}" }
                _syncState.value = SyncState(SyncStatus.FAILURE, error = e)
            }
        }

    private suspend fun syncDomains(root: RootManifest): Boolean =
        coroutineScope {
            root.domains
                .map { (domain, info) ->
                    async {
                        AppLogger.d(tag) { "Checking domain: $domain" }
                        val localDomainManifest = localStore.getDomainManifest(domain) ?: bundledSource.getDomainManifest(domain)

                        val localVersion = localDomainManifest?.contentVersion ?: -1
                        AppLogger.d(tag) { "Domain $domain: Local version = $localVersion, Remote version = ${info.contentVersion}" }

                        if (localDomainManifest == null || info.contentVersion > localDomainManifest.contentVersion) {
                            AppLogger.i(tag) { "Updating domain: $domain (v$localVersion -> v${info.contentVersion})" }
                            val remoteDomainManifest = remoteSource.fetchDomainManifest(info.manifest)
                            val domainUpdated = syncFiles(domain, remoteDomainManifest, localDomainManifest)
                            localStore.saveDomainManifest(remoteDomainManifest)
                            if (domainUpdated) {
                                _onDomainUpdated.emit(domain)
                            }
                            domainUpdated
                        } else {
                            AppLogger.d(tag) { "Domain $domain is up to date." }
                            false
                        }
                    }
                }.awaitAll()
                .any { it }
        }

    private suspend fun syncFiles(
        domain: String,
        remoteManifest: DomainManifest,
        localManifest: DomainManifest?,
    ): Boolean =
        coroutineScope {
            val localFiles = localManifest?.files?.associateBy { it.location } ?: emptyMap()
            val semaphore = Semaphore(5) // Limit concurrent downloads
            val mutex = Mutex()
            var syncedCount = 0
            var updatedAny = false
            val totalFiles = remoteManifest.files.size

            // Track current state of files for checkpoints
            val currentFilesMap = (localManifest?.files ?: emptyList()).associateBy { it.location }.toMutableMap()

            remoteManifest.files
                .map { remoteFile ->
                    async {
                        val localFile = localFiles[remoteFile.location]
                        val needsDownload =
                            localFile == null ||
                                localFile.checksum != remoteFile.checksum ||
                                !localStore.exists(remoteFile.location)

                        if (needsDownload) {
                            semaphore.withPermit {
                                AppLogger.v(tag) { "Downloading: ${remoteFile.location}" }
                                try {
                                    val content = remoteSource.downloadFile(remoteFile.location)
                                    localStore.writeFile(remoteFile.location, content)

                                    mutex.withLock {
                                        updatedAny = true
                                        currentFilesMap[remoteFile.location] = remoteFile
                                    }
                                } catch (e: Exception) {
                                    AppLogger.e(tag, e) { "Failed to download ${remoteFile.location}" }
                                    throw e
                                }
                            }
                        } else {
                            mutex.withLock {
                                currentFilesMap[remoteFile.location] = remoteFile
                            }
                        }

                        val current =
                            mutex.withLock {
                                syncedCount++

                                // Checkpoint: Save manifest every 50 files
                                if (syncedCount % 50 == 0 && updatedAny) {
                                    val checkpointManifest =
                                        remoteManifest.copy(
                                            files = currentFilesMap.values.toList(),
                                        )
                                    localStore.saveDomainManifest(checkpointManifest)
                                    AppLogger.d(tag) { "Checkpoint [$domain]: Saved manifest at $syncedCount files." }
                                }
                                syncedCount
                            }

                        if (current % 100 == 0 || current == totalFiles) {
                            AppLogger.d(tag) { "Progress [$domain]: $current / $totalFiles files processed." }
                        }
                    }
                }.awaitAll()
            updatedAny
        }
}
