package com.paradox543.malankaraorthodoxliturgica.data.sync.domain

/**
 * Interface to be implemented in Swift using the official Firebase iOS SDK.
 */
interface NativeRemoteContentSource {
    suspend fun fetchRootManifest(): String
    suspend fun fetchDomainManifest(path: String): String
    suspend fun downloadFile(path: String): String
}
