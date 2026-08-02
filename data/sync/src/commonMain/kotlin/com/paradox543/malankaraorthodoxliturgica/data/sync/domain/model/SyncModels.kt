package com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class RootManifest(
    val version: Int,
    val domains: Map<String, DomainManifestInfo>
)

@Serializable
data class DomainManifestInfo(
    val version: Int,
    val path: String
)

@Serializable
data class DomainManifest(
    val domain: String,
    val contentVersion: Int,
    val files: List<FileEntry>
)

@Serializable
data class FileEntry(
    val name: String,
    val location: String,
    val checksum: String,
    val sizeBytes: Long
)

enum class SyncStatus {
    IDLE, SYNCING, SUCCESS, FAILURE
}

data class SyncState(
    val status: SyncStatus,
    val progress: Float = 0f,
    val error: Throwable? = null
)
