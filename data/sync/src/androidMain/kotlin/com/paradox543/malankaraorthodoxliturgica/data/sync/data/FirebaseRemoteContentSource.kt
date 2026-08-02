package com.paradox543.malankaraorthodoxliturgica.data.sync.data

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.RemoteContentSource
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.DomainManifest
import com.paradox543.malankaraorthodoxliturgica.data.sync.domain.model.RootManifest
import com.paradox543.malankaraorthodoxliturgica.logging.AppLogger
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json

class FirebaseRemoteContentSource(
    private val storage: FirebaseStorage,
    private val json: Json
) : RemoteContentSource {
    private val tag = "RemoteSource"
    private val maxDownloadSize: Long = 20 * 1024 * 1024 // 20MB limit for any single liturgical file

    override suspend fun fetchRootManifest(): RootManifest {
        AppLogger.d(tag) { "Fetching remote root manifest..." }
        return try {
            val bytes = storage.reference.child("manifest.json").getBytes(maxDownloadSize).await()
            json.decodeFromString(bytes.decodeToString())
        } catch (e: StorageException) {
            AppLogger.e(tag, e) { "Firebase Storage error fetching root manifest. Code: ${e.errorCode}" }
            throw e
        }
    }

    override suspend fun fetchDomainManifest(path: String): DomainManifest {
        AppLogger.d(tag) { "Fetching remote domain manifest: $path" }
        return try {
            val bytes = storage.reference.child(path).getBytes(maxDownloadSize).await()
            json.decodeFromString(bytes.decodeToString())
        } catch (e: StorageException) {
            AppLogger.e(tag, e) { "Firebase Storage error fetching domain manifest ($path). Code: ${e.errorCode}" }
            throw e
        }
    }

    override suspend fun downloadFile(path: String): String {
        AppLogger.v(tag) { "Downloading remote file: $path" }
        return try {
            val bytes = storage.reference.child(path).getBytes(maxDownloadSize).await()
            bytes.decodeToString()
        } catch (e: StorageException) {
            AppLogger.e(tag, e) { "Firebase Storage error downloading file ($path). Code: ${e.errorCode}" }
            throw e
        }
    }
}
